package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.data.LockedInfo

internal object Common : Widget<Component.Builder<*>>() {

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
            val yogaEdge = YogaEdge.valueOf(edge.toUpperCase())
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
    }

    override fun onCreate(c: ComponentContext, lockedInfo: LockedInfo): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}