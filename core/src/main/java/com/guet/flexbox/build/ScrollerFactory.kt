package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll

internal object ScrollerFactory : WidgetFactory<Component.Builder<*>>() {

    init {
        bool("scrollBarEnable") {
            if (this is HorizontalScroll.Builder) {
                scrollbarEnabled(it)
            } else if (this is VerticalScroll.Builder) {
                scrollbarEnabled(it)
            }
        }
    }

    override fun create(c: BuildContext, attrs: Map<String, String>): Component.Builder<*> {
        return if (c.tryGetValue(attrs["orientation"], String::class.java, "vertical") == "horizontal") {
            HorizontalScroll.create(c.componentContext)
        } else {
            VerticalScroll.create(c.componentContext)
        }.apply {
            applyDefault(c, attrs)
        }
    }

    override fun Component.Builder<*>.applyChildren(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<Component.Builder<*>>
    ) {
        if (children.isNotEmpty()) {
            if (this is HorizontalScroll.Builder) {
                contentProps(children.single())
            } else if (this is VerticalScroll.Builder) {
                childComponent(children.single())
            }
        }
    }
}