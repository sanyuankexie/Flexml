package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll

internal object ScrollerFactory : WidgetFactory<Component.Builder<*>>() {

    internal enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private val orientations = mapOf(
            "vertical" to Orientation.VERTICAL,
            "horizontal" to Orientation.HORIZONTAL
    )

    init {
        boolAttr("scrollBarEnable") { _, it ->
            if (this is HorizontalScroll.Builder) {
                scrollbarEnabled(it)
            } else if (this is VerticalScroll.Builder) {
                scrollbarEnabled(it)
            }
        }
    }

    override fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Component.Builder<*> {
        return if (attrs != null && c.tryGetEnum(
                        attrs["orientation"],
                        orientations
                ) == Orientation.HORIZONTAL) {
            HorizontalScroll.create(c.componentContext)
        } else {
            VerticalScroll.create(c.componentContext)
        }
    }

    override fun onInstallChildren(
            owner: Component.Builder<*>,
            c: BuildContext,
            attrs: Map<String, String>?,
            children: List<Component>?,
            visibility: Int
    ) {
        if (!children.isNullOrEmpty()) {
            if (owner is HorizontalScroll.Builder) {
                owner.contentProps(children.single())
            } else if (owner is VerticalScroll.Builder) {
                owner.childComponent(children.single())
            }
        }
    }

}