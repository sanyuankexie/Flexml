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
            to: Binding?,
            type: String,
            attrs: Map<String, Any>,
            pageContext: PageContext,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
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