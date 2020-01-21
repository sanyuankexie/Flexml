package com.guet.flexbox.litho

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.guet.flexbox.FlexAlign
import com.guet.flexbox.FlexDirection
import com.guet.flexbox.FlexJustify
import com.guet.flexbox.FlexWrap
import com.guet.flexbox.build.AttributeSet

internal object ToFlex : ToComponent<Component.ContainerBuilder<*>>(Common) {

    override val attributeAssignSet: AttributeAssignSet<Component.ContainerBuilder<*>> by create {
        register("flexWrap") { _, _, value: FlexWrap ->
            wrap(value.mapValue())
        }
        register("justifyContent") { _, _, value: FlexJustify ->
            justifyContent(value.mapValue())
        }
        register("alignItems") { _, _, value: FlexAlign ->
            alignItems(value.mapValue())
        }
        register("alignContent") { _, _, value: FlexAlign ->
            alignContent(value.mapValue())
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