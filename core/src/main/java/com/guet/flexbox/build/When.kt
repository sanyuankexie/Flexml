package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

internal object When : Declaration() {
    override val attributeSet: AttributeSet
        get() = emptyMap()

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
        var elseItem: TemplateNode? = null
        for (item in children) {
            if (item.type == "case") {
                val itemAttrs = item.attrs
                if (itemAttrs != null && bindings.bindAttr(
                                "if",
                                itemAttrs,
                                pageContext,
                                data
                        )["test"] == true) {
                    return item.children?.map {
                        bindings.bindNode(
                                it,
                                pageContext,
                                data,
                                upperVisibility,
                                other
                        )
                    }?.flatten() ?: emptyList()
                }
            } else if (item.type == "else" && elseItem == null) {
                elseItem = item
            }
        }
        return elseItem?.children?.map {
            bindings.bindNode(
                    it,
                    pageContext,
                    data,
                    upperVisibility,
                    other
            )
        }?.flatten() ?: emptyList()
    }

}