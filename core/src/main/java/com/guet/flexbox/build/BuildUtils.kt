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
        return buildMap[name]?.let {
            bindAttr(it, attrs, pageContext, data)
        } ?: emptyMap()
    }

    private fun bindAttr(
            toWidget: ToWidget,
            attrs: Map<String, String>,
            pageContext: PageContext,
            data: PropsELContext
    ): Map<String, Any> {
        return if (attrs.isNullOrEmpty()) {
            emptyMap()
        } else {
            HashMap<String, Any>(attrs.size).apply {
                for ((key, raw) in attrs) {
                    val result = toWidget[key]
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
        val toWidget: ToWidget = buildMap[type] ?: default
        val values = templateNode.attrs?.let {
            bindAttr(toWidget, it, pageContext, data)
        } ?: emptyMap()
        val children = templateNode.children ?: emptyList()
        return toWidget.toWidget(
                this,
                type,
                values,
                pageContext,
                data,
                children,
                upperVisibility,
                c
        )
    }

    protected abstract val buildMap: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }
}
