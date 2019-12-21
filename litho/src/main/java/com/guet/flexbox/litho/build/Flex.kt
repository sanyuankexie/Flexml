package com.guet.flexbox.litho.build

import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap
import com.guet.flexbox.content.RenderNode

internal object Flex : Widget<Component.ContainerBuilder<*>>(Common) {

    override val attributeSet: AttributeSet<Component.ContainerBuilder<*>> by com.guet.flexbox.litho.build.create {
        this["flexWrap"] = object : Assignment<Component.ContainerBuilder<*>, YogaWrap>() {
            override fun Component.ContainerBuilder<*>.assign(display: Boolean, other: Map<String, Any>, value: YogaWrap) {
                wrap(value)
            }
        }
        this["justifyContent"] = object : Assignment<Component.ContainerBuilder<*>, YogaJustify>() {
            override fun Component.ContainerBuilder<*>.assign(display: Boolean, other: Map<String, Any>, value: YogaJustify) {
                justifyContent(value)
            }
        }
        this["alignItems"] = object : Assignment<Component.ContainerBuilder<*>, YogaAlign>() {
            override fun Component.ContainerBuilder<*>.assign(display: Boolean, other: Map<String, Any>, value: YogaAlign) {
                alignItems(value)
            }
        }
        this["alignContent"] = object : Assignment<Component.ContainerBuilder<*>, YogaAlign>() {
            override fun Component.ContainerBuilder<*>.assign(display: Boolean, other: Map<String, Any>, value: YogaAlign) {
                alignContent(value)
            }
        }
    }

    override fun onCreate(
            c: ComponentContext,
            renderNode: RenderNode
    ): Component.ContainerBuilder<*> {
        val component: Component.ContainerBuilder<*>
        when (renderNode.attrs.getOrElse("flexDirection") { YogaFlexDirection.ROW }) {
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
            renderNode: RenderNode,
            children: List<Component>
    ) {
        children.forEach {
            owner.child(it)
        }
    }
}