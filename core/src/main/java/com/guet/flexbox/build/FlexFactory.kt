package com.guet.flexbox.build

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify.*
import com.facebook.yoga.YogaWrap.*

internal object FlexFactory : WidgetFactory<Component.ContainerBuilder<*>>() {

    private val flexDirections = mapOf(
            "row" to YogaFlexDirection.ROW,
            "column" to YogaFlexDirection.COLUMN,
            "rowReverse" to YogaFlexDirection.ROW_REVERSE,
            "columnReverse" to YogaFlexDirection.COLUMN_REVERSE
    )

    init {
        enumAttr("flexWrap",
                mapOf(
                        "wrap" to WRAP,
                        "noWrap" to NO_WRAP,
                        "wrapReverse" to WRAP_REVERSE
                )
        ) { _, it ->
            wrap(it)
        }
        enumAttr("justifyContent",
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
        enumAttr("alignItems",
                mapOf(
                        "auto" to YogaAlign.AUTO,
                        "flexStart" to YogaAlign.FLEX_START,
                        "flexEnd" to YogaAlign.FLEX_END,
                        "center" to YogaAlign.CENTER,
                        "baseline" to YogaAlign.BASELINE,
                        "stretch" to YogaAlign.STRETCH
                )
        ) { _, it ->
            alignItems(it)
        }
        enumAttr("alignContent",
                mapOf(
                        "auto" to YogaAlign.AUTO,
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

    override fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        val type = if (attrs != null) {
            c.scope(flexDirections) {
                c.tryGetValue(attrs["flexDirection"], YogaFlexDirection.ROW)
            }
        } else {
            YogaFlexDirection.ROW
        }
        when (type) {
            YogaFlexDirection.COLUMN -> {
                component = Column.create(c.componentContext)
            }
            YogaFlexDirection.ROW -> {
                component = Row.create(c.componentContext)
            }
            YogaFlexDirection.COLUMN_REVERSE -> {
                component = Column.create(c.componentContext)
                        .reverse(true)
            }
            YogaFlexDirection.ROW_REVERSE -> {
                component = Row.create(c.componentContext)
                        .reverse(true)
            }
            else -> {
                component = Row.create(c.componentContext)
            }
        }
        return component
    }

    override fun onInstallChildren(
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