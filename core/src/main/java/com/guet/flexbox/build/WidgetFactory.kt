package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.CallSuper
import com.bumptech.glide.request.target.Target
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.widget.BorderDrawable
import com.guet.flexbox.widget.NetworkDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*

internal abstract class WidgetFactory<T : Component.Builder<*>> : Transform {

    internal val mappings = Mappings<T>()

    init {
        numberAttr<Double>("width") { _, _, it ->
            this.widthPx(it.toPx())
        }
        numberAttr<Double>("height") { _, _, it ->
            this.heightPx(it.toPx())
        }
        numberAttr<Float>("flexGrow") { _, _, it ->
            this.flexGrow(it)
        }
        numberAttr<Float>("flexShrink") { _, _, it ->
            this.flexShrink(it)
        }
        enumAttr("alignSelf",
                mapOf(
                        "auto" to YogaAlign.AUTO,
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { _, _, it ->
            this.alignSelf(it)
        }
        numberAttr<Double>("margin") { _, _, it ->
            this.marginPx(YogaEdge.ALL, it.toPx())
        }
        numberAttr<Double>("padding") { _, _, it ->
            this.paddingPx(YogaEdge.ALL, it.toPx())
        }
        for (index in edges.indices) {
            val yogaEdge = YogaEdge.valueOf(edges[index].toUpperCase(Locale.US))
            numberAttr<Double>("margin" + edges[index]) { map, _, it ->
                if (!map.containsKey("margin")) {
                    this.marginPx(yogaEdge, it.toPx())
                }
            }
            numberAttr<Double>("padding" + edges[index]) { map, _, it ->
                if (!map.containsKey("padding")) {
                    this.paddingPx(yogaEdge, it.toPx())
                }
            }
        }
    }

    final override fun transform(
            c: ComponentContext,
            dataBinding: DataContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component> {
        val value = create(c, dataBinding, nodeInfo, upperVisibility)
        return if (value != null) {
            Collections.singletonList(value)
        } else {
            emptyList()
        }
    }

    protected abstract fun onCreateWidget(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        if (!attrs.isNullOrEmpty()) {
            owner.applyEvent(c, dataBinding, attrs, visibility)
            if (visibility == View.VISIBLE) {
                owner.applyBackground(c, dataBinding, attrs)
            }
        }
        val display = visibility == View.VISIBLE
        if (!attrs.isNullOrEmpty()) {
            for ((key, value) in attrs) {
                mappings[key]?.invoke(owner, dataBinding, attrs, display, value)
            }
        }
    }

    private fun create(
            c: ComponentContext,
            dataBinding: DataContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): Component? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateVisibility(dataBinding, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, dataBinding, attrs, visibility)
        onLoadStyles(builder, c, dataBinding, attrs, visibility)
        onInstallChildren(builder, c, dataBinding, attrs, childrenNodes?.map {
            c.createFromElement(dataBinding, it, visibility)
        }?.flatten(), visibility)
        return builder.build()
    }

    private fun T.applyBackground(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>
    ) {
        val borderRadius = dataBinding.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = dataBinding.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = dataBinding.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var backgroundDrawable: Drawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ColorDrawable(dataBinding.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = dataBinding.scope(orientations) {
                    dataBinding.scope(colorNameMap) {
                        dataBinding.tryGetValue<Any>(background, Unit)
                    }
                }
                if (backgroundELResult is Drawable) {
                    backgroundDrawable = backgroundELResult
                } else if (backgroundELResult is CharSequence && backgroundELResult.isNotEmpty()) {
                    var width = dataBinding.tryGetValue(attrs["width"], Target.SIZE_ORIGINAL)
                    if (width <= 0) {
                        width = Target.SIZE_ORIGINAL
                    }
                    var height = dataBinding.tryGetValue(attrs["height"], Target.SIZE_ORIGINAL)
                    if (height <= 0) {
                        height = Target.SIZE_ORIGINAL
                    }
                    backgroundDrawable = NetworkDrawable(
                            width.toPx(),
                            height.toPx(),
                            c.androidContext,
                            backgroundELResult
                    )
                }
            }
        }
        if (backgroundDrawable == null) {
            backgroundDrawable = NoOpDrawable()
        }
        @Suppress("DEPRECATION")
        this.background(BorderDrawable(
                backgroundDrawable,
                borderRadius,
                borderWidth,
                borderColor
        ))
    }

    private fun T.applyEvent(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        val clickUrl = dataBinding.tryGetValue(attrs["clickUrl"], "")
        val reportClick = dataBinding.tryGetValue(attrs["reportClick"], "")
        if (clickUrl.isNotEmpty()) {
            clipChildren(false)
            clickHandler(DynamicBox.onClick(
                    c,
                    clickUrl,
                    reportClick
            ))
        }
        val reportView = dataBinding.tryGetValue(attrs["reportView"], "")
        if (reportView.isNotEmpty() && display) {
            visibleHandler(DynamicBox.onView(
                    c,
                    reportView))
        }
    }

    protected open fun calculateVisibility(
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            upperVisibility: Int
    ): Int {
        return if (upperVisibility == View.VISIBLE
                && attrs != null) {
            dataBinding.scope(visibilityValues) {
                dataBinding.tryGetValue(
                        attrs["visibility"],
                        View.VISIBLE
                )
            }
        } else {
            upperVisibility
        }
    }

    protected inline fun <reified V : Any> scopeAttr(
            name: String,
            scope: Map<String, V>,
            fallback: V,
            crossinline action: Apply<T, V>
    ) {
        mappings[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.scope(scope) {
                    c.tryGetValue(value, fallback)
                }
            } else {
                scope[value] ?: fallback
            })
        }
    }

    protected inline fun <reified V : Enum<V>> enumAttr(
            name: String,
            scope: Map<String, V>,
            fallback: V = V::class.java.enumConstants[0],
            crossinline action: Apply<T, V>
    ) {
        scopeAttr(name, scope, fallback, action)
    }

    protected inline fun textAttr(
            name: String,
            fallback: String = "",
            crossinline action: Apply<T, String>) {
        mappings[name] = { c, map, display, value ->
            action(map, display, c.tryGetValue(value, fallback))
        }
    }

    protected inline fun boolAttr(
            name: String,
            fallback: Boolean = false,
            crossinline action: Apply<T, Boolean>) {
        mappings[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.tryGetValue(value, fallback)
            } else {
                try {
                    value.toBoolean()
                } catch (e: Exception) {
                    fallback
                }
            })
        }
    }

    protected inline fun <reified N : Number> numberAttr(
            name: String,
            fallback: N = 0.safeCast(),
            crossinline action: Apply<T, N>) {
        mappings[name] = { c, map, display, value ->
            action(map, display, c.tryGetValue(value, fallback))
        }
    }

    protected inline fun colorAttr(
            name: String,
            fallback: Int = Color.TRANSPARENT,
            crossinline action: Apply<T, Int>) {
        mappings[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.tryGetColor(value, fallback)
            } else {
                try {
                    Color.parseColor(value)
                } catch (e: Exception) {
                    fallback
                }
            })
        }
    }

    internal companion object {

        internal val edges = arrayOf("Left", "Right", "Top", "Bottom")

        @Suppress("UNCHECKED_CAST")
        internal val colorNameMap = (Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, *>)
                .map { it.key to it.key }.toMap()

        internal val visibilityValues = mapOf(
                "visible" to View.VISIBLE,
                "invisible" to View.INVISIBLE,
                "gone" to View.GONE
        )

        internal val orientations: Map<String, Any> = mapOf(
                "t2b" to GradientDrawable.Orientation.TOP_BOTTOM,
                "tr2bl" to GradientDrawable.Orientation.TR_BL,
                "l2r" to GradientDrawable.Orientation.LEFT_RIGHT,
                "br2tl" to GradientDrawable.Orientation.BR_TL,
                "b2t" to GradientDrawable.Orientation.BOTTOM_TOP,
                "r2l" to GradientDrawable.Orientation.RIGHT_LEFT,
                "tl2br" to GradientDrawable.Orientation.TL_BR
        )
    }
}