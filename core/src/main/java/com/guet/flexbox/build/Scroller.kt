package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.Orientation

internal object Scroller : Widget<Component.Builder<*>>(Common) {

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
    }

    override fun onCreate(c: ComponentContext, lockedInfo: LockedInfo): Component.Builder<*> {
        return when (lockedInfo.attrs.getOrElse("orientation") { Orientation.HORIZONTAL }) {
            Orientation.HORIZONTAL -> {
                HorizontalScroll.create(c)
            }
            else -> {
                VerticalScroll.create(c)
            }
        }
    }

    override fun onInstallChildren(owner: Component.Builder<*>, lockedInfo: LockedInfo, children: List<Component>) {
        if (children.isNotEmpty()) {
            if (owner is HorizontalScroll.Builder) {
                owner.contentProps(children.single())
            } else if (owner is VerticalScroll.Builder) {
                owner.childComponent(children.single())
            }
        }
    }
}