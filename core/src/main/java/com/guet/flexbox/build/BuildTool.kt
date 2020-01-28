package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

abstract class BuildTool {

    protected abstract val widgets: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }

    fun build(
            templateNode: TemplateNode,
            hostContext: HostContext,
            data: ELContext,
            c: Any
    ): Child {
        return buildAll(
                listOf(templateNode),
                hostContext,
                data,
                true,
                c
        ).single()
    }

    internal fun buildAll(
            templates: List<TemplateNode>,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        if (templates.isEmpty()) {
            return emptyList()
        }
        return templates.map { templateNode ->
            val type = templateNode.type
            val toWidget: ToWidget = widgets[type] ?: default
            toWidget.toWidget(
                    this@BuildTool,
                    templateNode,
                    hostContext,
                    data,
                    upperVisibility,
                    other
            )
        }.flatten()
    }
}
