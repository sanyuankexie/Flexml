package com.guet.flexbox.build

import android.graphics.Color
import androidx.collection.ArrayMap
import com.facebook.litho.Border
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.el.ELException
import com.guet.flexbox.widget.AsyncDrawable
import org.dom4j.Attribute
import org.dom4j.Document
import org.dom4j.Element
import java.util.*

internal abstract class Factory<T : Component.Builder<*>> : Behavior {

    private val mappings = ArrayMap<String, T.(BuildContext, String) -> Unit>()

    init {
        value("width") {
            this.widthPx(it.toPx())
        }
        value("height") {
            this.heightPx(it.toPx())
        }
        value("margin") {
            this.marginPx(YogaEdge.ALL, it.toPx())
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
        value("flexGrow") {
            this.flexGrow(it.toFloat())
        }
        value("flexShrink") {
            this.flexShrink(it.toFloat())
        }
        text("clickUrl") {

        }
        value("borderRadius") {

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
    }

    private fun create(
            c: BuildContext,
            attrs: List<Attribute>,
            children: List<Component.Builder<*>>): T {
        val builder = create(c, attrs)
        builder.applyChildren(c, attrs, children)
        if (attrs.isNotEmpty()) {
            builder.applyEvent(c, attrs)
            builder.applyBackground(c, attrs)
        }
        return builder
    }

    override fun apply(
            c: BuildContext,
            element: Element,
            attrs: List<Attribute>,
            children: List<Component.Builder<*>>): List<Component.Builder<*>> {
        return Collections.singletonList(create(c, attrs, children))
    }

    protected fun T.applyDefault(
            c: BuildContext,
            attrs: List<Attribute>) {
        if (!attrs.isNullOrEmpty()) {
            for (attr in attrs) {
                val mapping = mappings[attr.name]
                if (mapping != null && attr.value != null) {
                    mapping.invoke(this, c, attr.value)
                }
            }
        }
    }

    protected abstract fun create(
            c: BuildContext,
            attrs: List<Attribute>): T

    protected open fun T.applyChildren(
            c: BuildContext,
            attrs: List<Attribute>,
            children: List<Component.Builder<*>>) {}

    private fun T.applyEvent(c: BuildContext, attrs: List<Attribute>) {
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
        } else {
            clickHandler(null)
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
            attrs: List<Attribute>) {
        val borderRadius = c.getValue(attrs["borderRadius"], Int::class.java, 0).toPx()
        val borderWidth = c.getValue(attrs["borderWidth"], Int::class.java, 0)
        val borderColor = c.getColor(attrs["borderColor"], Color.TRANSPARENT)
        if (borderRadius > 0) {
            border(Border.create(c.componentContext)
                    .color(YogaEdge.ALL, borderColor)
                    .radiusPx(borderRadius)
                    .widthPx(YogaEdge.ALL, borderWidth)
                    .build())
        }
        attrs["background"]?.let {
            try {
                val color = c.getColor(it)
                backgroundColor(color)
            } catch (e: Exception) {
                val url = c.getValue(it, String::class.java, "")
                if (url.isNotEmpty()) {
                    @Suppress("DEPRECATION")
                    background(AsyncDrawable(
                            c.componentContext.androidContext,
                            url,
                            borderRadius.toFloat()
                    ))
                }
            }
            return
        }
    }

    protected fun <V : Any> bound(
            name: String,
            fallback: V,
            map: Map<String, V>,
            action: T.(V) -> Unit
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
            action(c.getValue(value, String::class.java, fallback))
        }
    }

    protected inline fun bool(
            name: String,
            fallback: Boolean = false,
            crossinline action: T.(Boolean) -> Unit) {
        mappings[name] = { c, value ->
            action(c.getValue(value, Boolean::class.java, fallback))
        }
    }

    protected inline fun value(
            name: String, fallback: Double = 0.0,
            crossinline action: T.(Double) -> Unit) {
        mappings[name] = { c, value ->
            action(c.getValue(value, Double::class.java, fallback))
        }
    }

    protected inline fun color(
            name: String,
            fallback: Int = Color.TRANSPARENT,
            crossinline action: T.(Int) -> Unit) {
        mappings[name] = { c, value ->
            action(c.getColor(value, fallback))
        }
    }

    companion object {

        private val behaviors = ArrayMap<String, Behavior>()

        init {
            behaviors["Image"] = ImageFactory
            behaviors["Flex"] = FlexFactory
            behaviors["Text"] = TextFactory
            behaviors["Frame"] = FrameFactory
            behaviors["for"] = ForBehavior
        }

        internal fun createFromElement(
                c: BuildContext,
                element: Element): List<Component.Builder<*>> {
            val behavior = behaviors.getValue(element.name)
            return behavior.apply(
                    c,
                    element,
                    element.attributes(),
                    element.elements().map {
                        createFromElement(c, it)
                    }.flatten())
        }

        @JvmStatic
        fun createFromTreeRoot(
                c: ComponentContext,
                document: Document,
                bind: Any?
        ): Component {
            return createFromElement(
                    BuildContext(c, bind),
                    document.rootElement
            )[0].build()
        }
    }
}