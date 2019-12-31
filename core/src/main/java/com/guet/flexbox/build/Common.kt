package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.event.ClickUrlEventHandler
import com.guet.flexbox.event.OnClickEventHandler
import java.util.*

internal object Common : ToComponent<Component.Builder<*>>() {

    override val attributeSet: AttributeSet<Component.Builder<*>> by create {
        this["width"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                widthPx(value.toPx())
            }
        }
        this["height"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                heightPx(value.toPx())
            }
        }
        this["minWidth"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                minWidthPx(value.toPx())
            }
        }
        this["maxWidth"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                maxWidthPx(value.toPx())
            }
        }
        this["minHeight"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                minHeightPx(value.toPx())
            }
        }
        this["maxWidth"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                maxHeightPx(value.toPx())
            }
        }
        this["flexGrow"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                flexGrow(value.toFloat())
            }
        }
        this["flexShrink"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                flexShrink(value.toFloat())
            }
        }
        this["alignSelf"] = object : Assignment<Component.Builder<*>, YogaAlign>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: YogaAlign) {
                alignSelf(value)
            }
        }
        this["margin"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                marginPx(YogaEdge.ALL, value.toPx())
            }
        }
        this["padding"] = object : Assignment<Component.Builder<*>, Double>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                paddingPx(YogaEdge.ALL, value.toPx())
            }
        }
        for (edge in arrayOf("Left", "Right", "Top", "Bottom")) {
            val yogaEdge = YogaEdge.valueOf(edge.toUpperCase(Locale.US))
            this["margin$edge"] = object : Assignment<Component.Builder<*>, Double>() {
                override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                    marginPx(yogaEdge, value.toPx())
                }
            }
            this["padding$edge"] = object : Assignment<Component.Builder<*>, Double>() {
                override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Double) {
                    paddingPx(yogaEdge, value.toPx())
                }
            }
        }
        this["clickUrl"] = object : Assignment<Component.Builder<*>, ClickUrlEventHandler>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: ClickUrlEventHandler) {
                if (!other.containsKey("onClick")) {
                    clickHandler(value)
                }
            }
        }
        this["onClick"] = object : Assignment<Component.Builder<*>, OnClickEventHandler>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: OnClickEventHandler) {
                clickHandler(value)
            }
        }
    }

    override fun create(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}