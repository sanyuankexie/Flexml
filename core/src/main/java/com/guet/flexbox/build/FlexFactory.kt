package com.guet.flexbox.build

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify.*
import com.facebook.yoga.YogaWrap.*

internal object FlexFactory : WidgetFactory<Component.ContainerBuilder<*>>() {

    private val flexDirections = arrayOf("row", "column", "rowReverse", "columnReverse")
            .map {
                it to it
            }.toMap()

    init {
        enumAttr("flexWrap", NO_WRAP,
                mapOf(
                        "wrap" to WRAP,
                        "noWrap" to NO_WRAP,
                        "wrapReverse" to WRAP_REVERSE
                )
        ) { _, it ->
            wrap(it)
        }
        enumAttr("justifyContent", FLEX_START,
                mapOf(
                        "flexStart" to FLEX_START,
                        "flexEnd" to FLEX_END,
                        "center" to CENTER,
                        "spaceBetween" to SPACE_BETWEEN,
                        "spaceAround" to SPACE_AROUND
                )
        ) { _, it ->
            justifyContent(it)
        }
        enumAttr("alignItems", YogaAlign.FLEX_START,
                mapOf(
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { _, it ->
            alignItems(it)
        }
        enumAttr("alignContent", YogaAlign.FLEX_START,
                mapOf(
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { _, it ->
            alignContent(it)
        }
    }

    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        val type = if (attrs != null) {
            c.scope(flexDirections) {
                c.tryGetValue(attrs["flexDirection"], String::class.java, "row")
            }
        } else {
            "row"
        }
        if (type == "column") {
            component = Column.create(c.componentContext)
        } else {
            component = Row.create(c.componentContext)
        }
        if (type.endsWith("Reverse")) {
            component.reverse(true)
        }
        return component
    }

    override fun onApplyChildren(
            owner: Component.ContainerBuilder<*>,
            c: BuildContext,
            attrs: Map<String, String>?,
            children: List<Component.Builder<*>>?,
            visibility: Int
    ) {
        children?.forEach {
            owner.child(it)
        }
    }

}