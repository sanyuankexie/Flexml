package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

abstract class BuildUtils {

    fun bindAttr(
            name: String,
            attrs: Map<String, String>,
            pageContext: PageContext,
            data: PropsELContext
    ): Map<String, Any> {
        return attrs.let {
            HashMap<String, Any>(it.size).apply {
                for ((key, raw) in attrs) {
                    val result = bindingMap[name]
                            ?.first
                            ?.get(key)
                            ?.cast(pageContext, data, raw)
                    if (result != null) {
                        this[key] = result
                    }
                }
            }
        }
    }

    fun bindNode(
            templateNode: TemplateNode,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean = true,
            c: Any
    ): List<Any> {
        val type = templateNode.type
        val pair = bindingMap[type] ?: Common to null
        val values = templateNode.attrs?.let {
            bindAttr(type, it, pageContext, data)
        } ?: emptyMap()
        val children = templateNode.children ?: emptyList()
        return pair.first.transform(
                this,
                pair.second,
                type,
                values,
                pageContext,
                data,
                children,
                upperVisibility,
                c
        )
    }

    protected abstract val bindingMap: Map<String, Pair<Declaration, Binding?>>
}
