package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

abstract class BuildUtils {

    fun bindNode(
            templateNode: TemplateNode,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean = true,
            c: Any
    ): List<Any> {
        val type = templateNode.type
        val toWidget: ToWidget = buildMap[type] ?: default
        return toWidget.toWidget(
                this,
                templateNode,
                pageContext,
                data,
                upperVisibility,
                c
        )
    }

    protected abstract val buildMap: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }
}
