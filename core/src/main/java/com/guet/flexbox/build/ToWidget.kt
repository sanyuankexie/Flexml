package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import org.apache.commons.jexl3.JexlContext

class ToWidget(
        private val declaration: Declaration,
        private val factory: RenderNodeFactory<*>?
) {
    fun toWidget(
            bindings: BuildTool,
            template: TemplateNode,
            dataContext: JexlContext,
            pageContext: PageContext,
            other: Any?,
            upperVisibility: Boolean = true
    ): List<Any> {
        return declaration.transform(
                bindings,
                template.attrs ?: emptyMap(),
                template.children ?: emptyList(),
                factory,
                dataContext,
                pageContext,
                other,
                upperVisibility
        )
    }
}