package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.EventBridge
import com.guet.flexbox.R
import com.guet.flexbox.el.LambdaExpression
import java.util.*

internal object CommonAdapt : ComponentAdapt<Component.Builder<*>>() {

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
        this["clickUrl"] = object : Assignment<Component.Builder<*>, LambdaExpression>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: LambdaExpression) {
                if (!other.containsKey("onClick")) {
                    clickHandler(EventBridge.newEventHandler(R.id.on_click, value))
                }
            }
        }
        this["onClick"] = object : Assignment<Component.Builder<*>, LambdaExpression>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: LambdaExpression) {
                clickHandler(EventBridge.newEventHandler(R.id.on_click, value))
            }
        }
        this["onView"] = object : Assignment<Component.Builder<*>, LambdaExpression>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: LambdaExpression) {
                clickHandler(EventBridge.newEventHandler(R.id.on_visible, value))
            }
        }
    }

    override fun onCreate(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}