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

    fun bindAttrs(
            attrs: Map<String, String>?,
            pageContext: PageContext,
            data: PropsELContext
    ): Map<String, Any> {
        return if (attrs.isNullOrEmpty()) {
            emptyMap()
        } else {
            HashMap<String, Any>(attrs.size).also {
                for ((key, raw) in attrs) {
                    val result = this[key]?.cast(pageContext, data, raw)
                    if (result != null) {
                        it[key] = result
                    }
                }
            }
        }
    }

    internal open fun transform(
            bindings: BuildUtils,
            template: TemplateNode,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        return parent?.transform(
                bindings,
                template,
                factory,
                pageContext,
                data,
                upperVisibility,
                other
        ) ?: throw UnsupportedOperationException()
    }
}