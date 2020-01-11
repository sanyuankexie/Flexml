package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal object For : Declaration() {
    override val attributeSet: AttributeSet by create {
        text("var")
        value("from")
        value("to")
    }

    override fun transform(
            bindings: BuildUtils,
            template: TemplateNode,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        val children = template.children
        if (children.isNullOrEmpty()) {
            return emptyList()
        }
        val attrs = bindAttrs(
                template.attrs,
                pageContext,
                data
        )
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Double).toInt()
        val end = (attrs.getValue("to") as Double).toInt()
        return (from..end).map { index ->
            data.scope(mapOf(name to index)) {
                children.map {
                    bindings.bindNode(
                            it,
                            pageContext,
                            data,
                            upperVisibility,
                            other
                    )
                }
            }.flatten()
        }.flatten()
    }
}