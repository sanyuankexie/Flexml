package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll
import com.guet.flexbox.Orientation

internal object ScrollerAdapt : ComponentAdapt<Component.Builder<*>>(CommonAdapt) {

    override val attributeSet: AttributeSet<Component.Builder<*>> by create {
        this["scrollBarEnable"] = object : Assignment<Component.Builder<*>, Boolean>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Boolean) {
                if (this is HorizontalScroll.Builder) {
                    scrollbarEnabled(value)
                } else if (this is VerticalScroll.Builder) {
                    scrollbarEnabled(value)
                }
            }
        }
        this["fillViewport"] = object : Assignment<Component.Builder<*>, Boolean>() {
            override fun Component.Builder<*>.assign(display: Boolean, other: Map<String, Any>, value: Boolean) {
                if (this is HorizontalScroll.Builder) {
                    fillViewport(value)
                } else if (this is VerticalScroll.Builder) {
                    fillViewport(value)
                }
            }
        }
    }

    override fun onCreate(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): Component.Builder<*> {
        return when (attrs.getOrElse("orientation") { Orientation.HORIZONTAL }) {
            Orientation.HORIZONTAL -> {
                HorizontalScroll.create(c)
            }
            else -> {
                VerticalScroll.create(c)
            }
        }
    }

    override fun onInstallChildren(owner: Component.Builder<*>, type: String, visibility: Boolean, attrs: Map<String, Any>, children: List<Component>) {
        if (owner is HorizontalScroll.Builder) {
            owner.contentProps(children.single())
        } else if (owner is VerticalScroll.Builder) {
            owner.childComponent(children.single())
        }
    }
}