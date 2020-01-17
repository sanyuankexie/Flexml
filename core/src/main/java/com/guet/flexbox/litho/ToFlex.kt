package com.guet.flexbox.litho

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.build.AttributeSet

internal object ToFlex : ToComponent<Component.ContainerBuilder<*>>(Common) {

    override val attributeAssignSet: AttributeAssignSet<Component.ContainerBuilder<*>> by create {
        register("flexWrap") { _, _, value: YogaWrap ->
            wrap(value)
        }
        register("justifyContent") { _, _, value: YogaJustify ->
            justifyContent(value)
        }
        register("alignItems") { _, _, value: YogaAlign ->
            alignItems(value)
        }
        register("alignContent") { _, _, value: YogaAlign ->
            alignContent(value)
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        when (attrs.getOrElse("flexDirection") { YogaFlexDirection.ROW }) {
            YogaFlexDirection.COLUMN -> {
                component = Column.create(c)
            }
            YogaFlexDirection.COLUMN_REVERSE -> {
                component = Column.create(c)
                        .reverse(true)
            }
            YogaFlexDirection.ROW_REVERSE -> {
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