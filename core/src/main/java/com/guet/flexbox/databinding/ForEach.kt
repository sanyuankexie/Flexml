package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ToComponent
import com.guet.flexbox.el.PropsELContext

internal object ForEach : Declaration() {

    override val attributeSet: AttributeSet by create {
        text("var")
        typed<List<Any>>("items")
    }

    override fun transform(
            c: ComponentContext,
            to: ToComponent<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        val name = attrs.getValue("var") as String
        @Suppress("UNCHECKED_CAST")
        val items = attrs.getValue("items") as List<Any>
        return items.map {
            data.scope(mapOf(name to items)) {
                children.map {
                    Toolkit.bindNode(c, it, data, upperVisibility)
                }
            }.flatten()
        }.flatten()
    }
}