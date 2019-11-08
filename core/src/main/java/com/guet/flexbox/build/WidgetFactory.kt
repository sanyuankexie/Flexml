package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.CallSuper
import com.facebook.litho.Component
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.widget.BorderDrawable
import com.guet.flexbox.widget.NetworkDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*
import kotlin.collections.HashMap

internal abstract class WidgetFactory<T : Component.Builder<*>> : Transform {

    private val mappings = HashMap<String, T.(BuildContext, Boolean, String) -> Unit>()

    init {
        numberAttr<Int>("width") { _, it ->
            this.widthPx(it.toPx())
        }
        numberAttr<Int>("height") { _, it ->
            this.heightPx(it.toPx())
        }
        numberAttr<Int>("flexGrow") { _, it ->
            this.flexGrow(it.toFloat())
        }
        numberAttr<Int>("flexShrink") { _, it ->
            this.flexShrink(it.toFloat())
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
        ) { _, it ->
            this.alignSelf(it)
        }
        numberAttr<Int>("margin") { _, it ->
            this.marginPx(YogaEdge.ALL, it.toPx())
        }
        numberAttr<Int>("padding") { _, it ->
            this.paddingPx(YogaEdge.ALL, it.toPx())
        }
        val edges = arrayOf("Left", "Right", "Top", "Bottom")
        for (index in edges.indices) {
            val yogaEdge = YogaEdge.valueOf(edges[index].toUpperCase(Locale.US))
            numberAttr<Int>("margin" + edges[index]) { _, it ->
                this.marginPx(yogaEdge, it.toPx())
            }
            numberAttr<Int>("padding" + edges[index]) { _, it ->
                this.paddingPx(yogaEdge, it.toPx())
            }
        }
    }

    final override fun transform(
            c: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component.Builder<*>> {
        val value = create(c, nodeInfo, upperVisibility)
        return if (value != null) {
            Collections.singletonList<T>(value)
        } else {
            emptyList()
        }
    }

    protected abstract fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: BuildContext,
            attrs: Map<String, String>?,
            children: List<Component.Builder<*>>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        if (!attrs.isNullOrEmpty()) {
            for ((key, value) in attrs) {
                mappings[key]?.invoke(owner, c, display, value)
            }
        }
    }

    private fun create(
            c: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): T? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateOwnerVisibility(c, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, attrs, visibility)
        if (!attrs.isNullOrEmpty()) {
            builder.applyEvent(c, attrs, visibility)
            if (visibility != View.INVISIBLE) {
                builder.applyBackground(c, attrs)
            }
        }
        onLoadStyles(builder, c, attrs, visibility)
        onInstallChildren(builder, c, attrs, childrenNodes?.map {
            c.createFromElement(it, visibility)
        }?.flatten(), visibility)
        return builder
    }

    private fun T.applyEvent(
            c: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        var clickUrlValue: String? = null
        val clickUrl = attrs["clickUrl"]
        if (clickUrl != null) {
            clickUrlValue = c.getValue(clickUrl, String::class.java)
        }
        var reportClickValue: String? = null
        val reportClick = attrs["reportClick"]
        if (reportClick != null) {
            reportClickValue = c.getValue(reportClick, String::class.java)
        }
        if (!clickUrlValue.isNullOrEmpty()) {
            clipChildren(false)
            clickHandler(DynamicBox.onClick(
                    c.componentContext,
                    clickUrlValue,
                    reportClickValue
            ))
        }
        var reportViewValue: String? = null
        val reportView = attrs["reportView"]
        if (reportView != null) {
            reportViewValue = c.getValue(reportView, String::class.java)
        }
        if (!reportViewValue.isNullOrEmpty() && display) {
            visibleHandler(DynamicBox.onView(
                    c.componentContext,
                    reportClickValue))
        }
    }

    private fun T.applyBackground(
            c: BuildContext,
            attrs: Map<String, String>) {
        val borderRadius = c.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = c.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = c.tryGetColor(attrs["borderColor"],
                Color.TRANSPARENT)
        var model: Drawable? = null
        val backgroundValue = attrs["background"]
        if (backgroundValue != null) {
            try {
                model = ColorDrawable(c.getColor(backgroundValue))
            } catch (e: Exception) {
                val backgroundRaw = c.scope(orientations) {
                    c.scope(colorNameMap) {
                        c.tryGetValue<Any>(backgroundValue, Unit)
                    }
                }
                if (backgroundRaw is Drawable) {
                    model = backgroundRaw
                } else if (backgroundRaw is CharSequence && backgroundRaw.isNotEmpty()) {
                    model = NetworkDrawable(
                            c.componentContext.androidContext,
                            backgroundRaw
                    )
                }
            }
        }
        if (model == null) {
            model = NoOpDrawable
        }
        @Suppress("DEPRECATION")
        this.background(BorderDrawable(
                model,
                borderRadius,
                borderWidth,
                borderColor
        ))
    }

    private fun calculateOwnerVisibility(
            c: BuildContext,
            attrs: Map<String, String>?,
            upperVisibility: Int
    ): Int {
        return if (upperVisibility == View.VISIBLE
                && attrs != null) {
            c.scope(visibilityValues) {
                c.tryGetValue(
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
            crossinline action: T.(Boolean, V) -> Unit
    ) {
        mappings[name] = { c, display, value ->
            action(display, c.scope(scope) {
                c.tryGetValue(value, fallback)
            })
        }
    }

    protected inline fun <reified V : Enum<V>> enumAttr(
            name: String,
            scope: Map<String, V>,
            fallback: V = V::class.java.enumConstants[0],
            crossinline action: T.(Boolean, V) -> Unit
    ) {
        mappings[name] = { c, display, value ->
            action(display, c.scope(scope) {
                c.tryGetValue(value, fallback)
            })
        }
    }

    protected inline fun textAttr(
            name: String,
            fallback: String = "",
            crossinline action: T.(Boolean, String) -> Unit) {
        mappings[name] = { c, display, value ->
            action(display, c.tryGetValue(value, fallback))
        }
    }

    protected inline fun boolAttr(
            name: String,
            fallback: Boolean = false,
            crossinline action: T.(Boolean, Boolean) -> Unit) {
        mappings[name] = { c, display, value ->
            action(display, c.tryGetValue(value, fallback))
        }
    }

    protected inline fun <reified N : Number> numberAttr(
            name: String, fallback: N = 0 as N,
            crossinline action: T.(Boolean, N) -> Unit) {
        mappings[name] = { c, display, value ->
            action(display, c.tryGetValue(value, fallback))
        }
    }

    protected inline fun colorAttr(
            name: String,
            fallback: Int = Color.TRANSPARENT,
            crossinline action: T.(Boolean, Int) -> Unit) {
        mappings[name] = { c, display, value ->
            action(display, c.tryGetColor(value, fallback))
        }
    }

    internal companion object {

        @Suppress("UNCHECKED_CAST")
        internal val colorNameMap = (Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>)
                .map {
                    it.key to it.key
                }.toMap()

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