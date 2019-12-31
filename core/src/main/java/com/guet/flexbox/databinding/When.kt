package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ToComponent
import com.guet.flexbox.el.PropsELContext

internal object When : Declaration() {
    override val attributeSet: AttributeSet
        get() = emptyMap()

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
        var elseItem: TemplateNode? = null
        for (item in children) {
            if (item.type == "case") {
                val itemAttrs = item.attrs
                if (itemAttrs != null && Toolkit.bindAttr(
                                If,
                                itemAttrs,
                                pageContext,
                                data
                        )["test"] == true) {
                    return item.children?.map {
                        Toolkit.bindNode(c, it, pageContext, data, upperVisibility)
                    }?.flatten() ?: emptyList()
                }
            } else if (item.type == "else" && elseItem == null) {
                elseItem = item
            }
        }
        return elseItem?.children?.map {
            Toolkit.bindNode(c, it, pageContext, data, upperVisibility)
        }?.flatten() ?: emptyList()
    }

}