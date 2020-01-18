package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

abstract class BuildUtils {

    fun bindNode(
            templateNode: TemplateNode,
            pageContext: HostingContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            c: Any
    ): List<Child> {
        val type = templateNode.type
        val toWidget: ToWidget = widgets[type] ?: default
        return toWidget.toWidget(
                this,
                templateNode,
                pageContext,
                data,
                upperVisibility,
                c
        )
    }

    protected abstract val widgets: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }
}
