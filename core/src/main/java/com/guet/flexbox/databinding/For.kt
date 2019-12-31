package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ToComponent
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal object For : Declaration() {
    override val attributeSet: AttributeSet by create {
        text("var")
        value("from")
        value("to")
    }

    override fun transform(
            c: ComponentContext,
            to: ToComponent<*>?,
            type: String,
            attrs: Map<String, Any>,
            pageContext: PageContext,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Double).toInt()
        val to = (attrs.getValue("to") as Double).toInt()
        return (from..to).map { index ->
            data.scope(mapOf(name to index)) {
                children.map {
                    Toolkit.bindNode(c, it, pageContext, data, upperVisibility)
                }
            }.flatten()
        }.flatten()
    }
}