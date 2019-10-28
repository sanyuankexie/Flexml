package com.guet.flexbox.build

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify.*
import com.facebook.yoga.YogaWrap.*

internal object FlexFactory : WidgetFactory<Component.ContainerBuilder<*>>() {

    init {
        bound("flexWrap", NO_WRAP,
                mapOf(
                        "wrap" to WRAP,
                        "noWrap" to NO_WRAP,
                        "wrapReverse" to WRAP_REVERSE
                )
        ) {
            wrap(it)
        }
        bound("justifyContent", FLEX_START,
                mapOf(
                        "flexStart" to FLEX_START,
                        "flexEnd" to FLEX_END,
                        "center" to CENTER,
                        "spaceBetween" to SPACE_BETWEEN,
                        "spaceAround" to SPACE_AROUND
                )
        ) {
            justifyContent(it)
        }
        bound("alignItems", YogaAlign.FLEX_START,
                mapOf(
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) {
            alignItems(it)
        }
        bound("alignContent", YogaAlign.FLEX_START,
                mapOf(
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) {
            alignContent(it)
        }
    }

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>)
            : Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        val type = attrs["flexDirection"]
        if (type == "column") {
            component = Column.create(c.componentContext)
        } else {
            component = Row.create(c.componentContext)
        }
        if (type != null && type.endsWith("Reverse")) {
            component.reverse(true)
        }
        return component.apply {
            applyDefault(c, attrs)
        }
    }

    override fun Component.ContainerBuilder<*>.applyChildren(
            c: BuildContext, attrs: Map<String, String>,
            children: List<Component.Builder<*>>
    ) {
        children.forEach {
            child(it)
        }
    }
}