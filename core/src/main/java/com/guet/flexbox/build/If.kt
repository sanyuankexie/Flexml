package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

internal object If : Declaration() {

    override val attributeSet: AttributeSet by create {
        bool("test")
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
        if (attrs.getValue("test") as Boolean) {
            return children.map {
                bindings.bindNode(
                        it,
                        pageContext,
                        data,
                        upperVisibility,
                        other
                )
            }.flatten()
        }
        return emptyList()
    }
}