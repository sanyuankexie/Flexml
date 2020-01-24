package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object For : Declaration() {
    override val attributeInfoSet: AttributeInfoSet by create {
        text("var")
        value("from")
        value("to")
    }

    override fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: ViewFactory?,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Double).toInt()
        val end = (attrs.getValue("to") as Double).toInt()
        return buildTool.invokeAllTasks((from..end).map { index ->
            buildTool.createBuildTasks(
                    children,
                    hostContext,
                    data,
                    upperVisibility,
                    other,
                    mapOf(name to index)
            )
        }.flatten())
    }
}