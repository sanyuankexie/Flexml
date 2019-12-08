package com.luke.skywalker.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.VerticalScroll
import com.luke.skywalker.el.PropsELContext

internal object ScrollerFactory : WidgetFactory<Component.Builder<*>>(
        AttributeSet {
            boolAttr("scrollBarEnable") { _, _, it ->
                if (this is HorizontalScroll.Builder) {
                    scrollbarEnabled(it)
                } else if (this is VerticalScroll.Builder) {
                    scrollbarEnabled(it)
                }
            }
        }
) {

    internal enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private val orientations = mapOf(
            "vertical" to Orientation.VERTICAL,
            "horizontal" to Orientation.HORIZONTAL
    )

    override fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Component.Builder<*> {
        return if (attrs != null && data.tryGetEnum(
                        attrs["orientation"],
                        orientations
                ) == Orientation.HORIZONTAL) {
            HorizontalScroll.create(c)
        } else {
            VerticalScroll.create(c)
        }
    }

    override fun onInstallChildren(
            owner: Component.Builder<*>,
            c: ComponentContext,
            data: PropsELContext,
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