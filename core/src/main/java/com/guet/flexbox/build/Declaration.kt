package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

abstract class Declaration(
        private val parent: Declaration? = null
) {

    protected abstract val attributeSet: AttributeSet

    operator fun get(name: String): AttributeInfo<*>? {
        val v = attributeSet[name]
        if (v != null) {
            return v
        }
        if (parent != null) {
            return parent[name]
        }
        return null
    }

    internal open fun transform(
            bindings: BuildUtils,
            to: WidgetFactory?,
            type: String,
            attrs: Map<String, Any>,
            pageContext: PageContext,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        return parent?.transform(
                bindings,
                to,
                type,
                attrs,
                pageContext,
                data,
                children,
                upperVisibility,
                other
        ) ?: throw UnsupportedOperationException()
    }
}