package com.luke.skywalker.build

import android.graphics.Color
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.drawable.ComparableColorDrawable
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.luke.skywalker.DynamicBox
import com.luke.skywalker.NodeInfo
import com.luke.skywalker.el.LambdaExpression
import com.luke.skywalker.el.PropsELContext
import com.luke.skywalker.widget.AsyncLazyDrawable
import com.luke.skywalker.widget.BackgroundDrawable
import com.luke.skywalker.widget.NoOpDrawable

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal abstract class WidgetFactory<T : Component.Builder<*>>(
        extraProps: Map<String, Mapping<T>>
) : Transform {

    private val mappings = HashMap<String, Mapping<*>>()

    init {
        mappings.putAll(commonProps)
        mappings.putAll(extraProps)
    }

    final override fun transform(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>? {
        val component = create(c, data, nodeInfo, upperVisibility)
        if (component != null) {
            return listOf(component)
        }
        return null
    }


    protected abstract fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): T

    protected open fun onInstallChildren(
            owner: T,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int) {
    }

    @CallSuper
    protected open fun onLoadStyles(
            owner: T,
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        if (!attrs.isNullOrEmpty()) {
            owner.applyEvent(c, data, attrs, visibility)
            if (display) {
                owner.applyBackground(c, data, attrs)
            }
            for ((key, value) in attrs) {
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    (mappings[key] as? Mapping<Component.Builder<*>>)
                            ?.invoke(owner, data, attrs, display, value)
                }
            }
        }
    }

    private fun create(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): Component? {
        val attrs = nodeInfo.attrs
        val childrenNodes = nodeInfo.children
        val visibility = calculateVisibility(c, data, attrs, upperVisibility)
        if (visibility == View.GONE) {
            return null
        }
        val builder = onCreateWidget(c, data, attrs, visibility)
        onLoadStyles(builder, c, data, attrs, visibility)
        onInstallChildren(builder, c, data, attrs, childrenNodes?.map {
            data.inflate(c, it, visibility)
        }?.flatten(), visibility)
        return builder.build()
    }

    private fun T.applyBackground(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>
    ) {
        val borderRadius = data.tryGetValue(attrs["borderRadius"], 0).toPx()
        val borderWidth = data.tryGetValue(attrs["borderWidth"], 0).toPx()
        val borderColor = data.tryGetColor(attrs["borderColor"], Color.TRANSPARENT)
        var backgroundDrawable: ComparableDrawable? = null
        val background = attrs["background"]
        if (background != null) {
            try {
                backgroundDrawable = ComparableColorDrawable.create(data.getColor(background))
            } catch (e: Exception) {
                val backgroundELResult = data.tryGetValue(background, "")
                when (val model = parseUrl(c.androidContext, backgroundELResult)) {
                    is ComparableDrawable -> {
                        backgroundDrawable = model
                    }
                    is CharSequence, is Int -> {
                        backgroundDrawable = AsyncLazyDrawable(
                                c.androidContext,
                                model
                        )
                    }
                }
            }
        }
        if (backgroundDrawable == null) {
            backgroundDrawable = NoOpDrawable()
        }
        this.background(BackgroundDrawable(
                backgroundDrawable,
                borderRadius,
                borderWidth,
                borderColor
        ))
    }

    private fun T.applyEvent(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            visibility: Int
    ) {
        val display = visibility == View.VISIBLE
        val clickUrl = data.tryGetValue(attrs["clickUrl"], "")
        if (clickUrl.isNotEmpty()) {
            clickHandler(DynamicBox.onClick(
                    c,
                    data.tryGetLambda("()->sender.send('${this}')")
            ))
        } else {
            val onClick = data.tryGetLambda(attrs["onClick"])
            if (onClick != null) {
                clipChildren(false)
                clickHandler(DynamicBox.onClick(
                        c,
                        onClick
                ))
            }
        }
        val onView = data.tryGetValue<LambdaExpression?>(attrs["onView"], null)
        if (onView != null && display) {
            visibleHandler(DynamicBox.onView(
                    c,
                    onView
            ))
        }
    }

    protected open fun calculateVisibility(
            c: ComponentContext,
            pager: PropsELContext,
            attrs: Map<String, String>?,
            upperVisibility: Int
    ): Int {
        return if (upperVisibility == View.VISIBLE
                && attrs != null) {
            pager.scope(visibilityValues) {
                pager.tryGetValue(
                        attrs["visibility"],
                        View.VISIBLE
                )
            }
        } else {
            upperVisibility
        }
    }

    private companion object {

        internal val commonProps = AttributeSet<Component.Builder<*>> {
            numberAttr<Double>("borderWidth") { _, _, it ->
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
            val edges = arrayOf("Left", "Right", "Top", "Bottom")
            for (index in edges.indices) {
                val yogaEdge = YogaEdge.valueOf(edges[index].toUpperCase())
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

        @JvmStatic
        internal val visibilityValues = mapOf(
                "visible" to View.VISIBLE,
                "invisible" to View.INVISIBLE,
                "gone" to View.GONE
        )
    }
}