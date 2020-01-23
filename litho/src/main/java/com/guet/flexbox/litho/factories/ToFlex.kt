package com.guet.flexbox.litho.factories

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.enums.FlexDirection
import com.guet.flexbox.enums.FlexJustify
import com.guet.flexbox.enums.FlexWrap
import com.guet.flexbox.litho.ChildComponent

internal object ToFlex : ToComponent<Component.ContainerBuilder<*>>(Common) {

    override val attributeAssignSet: AttributeAssignSet<Component.ContainerBuilder<*>> by create {
        register("flexWrap") { _, _, value: FlexWrap ->
            wrap(value.mapToLithoValue())
        }
        register("justifyContent") { _, _, value: FlexJustify ->
            justifyContent(value.mapToLithoValue())
        }
        register("alignItems") { _, _, value: FlexAlign ->
            alignItems(value.mapToLithoValue())
        }
        register("alignContent") { _, _, value: FlexAlign ->
            alignContent(value.mapToLithoValue())
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        when (attrs.getOrElse("flexDirection") { FlexDirection.ROW }) {
            FlexDirection.COLUMN -> {
                component = Column.create(c)
            }
            FlexDirection.COLUMN_REVERSE -> {
                component = Column.create(c)
                        .reverse(true)
            }
            FlexDirection.ROW_REVERSE -> {
                component = Row.create(c)
                        .reverse(true)
            }
            else -> {
                component = Row.create(c)
            }
        }
        return component
    }

    override fun onInstallChildren(
            owner: Component.ContainerBuilder<*>,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        children.forEach {
            owner.child(it)
        }
    }
}