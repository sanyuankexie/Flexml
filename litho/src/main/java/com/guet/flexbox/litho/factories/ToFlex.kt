package com.guet.flexbox.litho.factories

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.enums.FlexDirection
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.filler.PropsFiller

internal object ToFlex : ToComponent<Component.ContainerBuilder<*>>() {

    override val propsFiller by PropsFiller
            .create<Component.ContainerBuilder<*>>(CommonProps) {
                enum("flexWrap", Component.ContainerBuilder<*>::wrap)
                enum("justifyContent", Component.ContainerBuilder<*>::justifyContent)
                enum("alignItems", Component.ContainerBuilder<*>::alignItems)
                enum("alignContent", Component.ContainerBuilder<*>::alignContent)
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
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
            attrs: PropSet,
            children: List<Widget>
    ) {
        children.forEach {
            owner.child(it)
        }
    }
}