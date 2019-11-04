package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.facebook.litho.Component
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.WidgetInfo
import com.guet.flexbox.el.ELException
import com.guet.flexbox.widget.BorderDrawable
import com.guet.flexbox.widget.NetworkDrawable
import com.guet.flexbox.widget.NoOpDrawable
import java.util.*
import kotlin.collections.HashMap

internal abstract class WidgetFactory<T : Component.Builder<*>> : Transform {

    private val mappings = HashMap<String, T.(BuildContext, String) -> Unit>()

    init {
        value("width") {
            this.widthPx(it.toPx())
        }
        value("height") {
            this.heightPx(it.toPx())
        }
        value("flexGrow") {
            this.flexGrow(it.toFloat())
        }
        value("flexShrink") {
            this.flexShrink(it.toFloat())
        }
        bound("alignSelf", YogaAlign.FLEX_START,
                mapOf(
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { this.alignSelf(it) }
        value("margin") {
            this.marginPx(YogaEdge.ALL, it.toPx())
        }
        value("padding") {
            this.paddingPx(YogaEdge.ALL, it.toPx())
        }
        val edges = arrayOf("Left", "Right", "Top", "Bottom")
        for (index in 0 until edges.size) {
            val yogaEdge = YogaEdge.valueOf(edges[index].toUpperCase())
            value("margin" + edges[index]) {
                this.marginPx(yogaEdge, it.toPx())
            }
            value("padding" + edges[index]) {
                this.paddingPx(yogaEdge, it.toPx())
            }
        }
    }

    private fun create(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<Component.Builder<*>>): T {
        val builder = create(c, attrs)
        builder.applyChildren(c, attrs, children)
        if (attrs.isNotEmpty()) {
            builder.applyEvent(c, attrs)
            builder.applyBackground(c, attrs)
        }
        return builder
    }

    override fun transform(
            c: BuildContext,
            widgetInfo: WidgetInfo,
            children: List<Component.Builder<*>>): List<Component.Builder<*>> {
        return Collections.singletonList(create(c, widgetInfo.attrs ?: emptyMap(), children))
    }

    protected fun T.applyDefault(
            c: BuildContext,
            attrs: Map<String, String>) {
        if (!attrs.isNullOrEmpty()) {
            for ((key, value) in attrs) {
                mappings[key]?.invoke(this, c, value)
            }
        }
    }

    protected abstract fun create(
            c: BuildContext,
            attrs: Map<String, String>): T

    protected open fun T.applyChildren(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<Component.Builder<*>>) {
    }

    private fun T.applyEvent(c: BuildContext, attrs: Map<String, String>) {
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
        if (!reportViewValue.isNullOrEmpty()) {
            visibleHandler(DynamicBox.onView(
                    c.componentContext,
                    reportClickValue
            ))
        }
    }

    private fun T.applyBackground(
            c: BuildContext,
            attrs: Map<String, String>) {
        val borderRadius = c.tryGetValue(attrs["borderRadius"],
                Int::class.java, 0).toPx()
        val borderWidth = c.tryGetValue(attrs["borderWidth"],
                Int::class.java, 0).toPx()
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
                        c.tryGetValue(backgroundValue, Any::class.java, Unit)
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

    protected inline fun <V : Any> bound(
            name: String,
            fallback: V,
            map: Map<String, V>,
            crossinline action: T.(V) -> Unit
    ) {
        mappings[name] = { c, value ->
            try {
                var result = map[c.getValue(value, String::class.java)]
                if (result == null) {
                    result = fallback
                }
                action(result)
            } catch (e: ELException) {
                action(fallback)
            }
        }
    }

    protected inline fun text(
            name: String,
            fallback: String = "",
            crossinline action: T.(String) -> Unit) {
        mappings[name] = { c, value ->
            action(c.tryGetValue(value, String::class.java, fallback))
        }
    }

    protected inline fun bool(
            name: String,
            fallback: Boolean = false,
            crossinline action: T.(Boolean) -> Unit) {
        mappings[name] = { c, value ->
            action(c.tryGetValue(value, Boolean::class.java, fallback))
        }
    }

    protected inline fun value(
            name: String, fallback: Double = 0.0,
            crossinline action: T.(Double) -> Unit) {
        mappings[name] = { c, value ->
            action(c.tryGetValue(value, Double::class.java, fallback))
        }
    }

    protected inline fun color(
            name: String,
            fallback: Int = Color.TRANSPARENT,
            crossinline action: T.(Int) -> Unit) {
        mappings[name] = { c, value ->
            action(c.tryGetColor(value, fallback))
        }
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        private val colorNameMap = (Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>)
                .map {
                    it.key to it.key as Any
                }.toMap()

        private val orientations: Map<String, Any> = mapOf(
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