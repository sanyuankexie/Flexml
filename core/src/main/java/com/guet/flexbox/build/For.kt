package com.guet.flexbox.build

import com.guet.flexbox.EventContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope

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
            factory: RenderNodeFactory?,
            eventContext: EventContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Float).toInt()
        val end = (attrs.getValue("to") as Float).toInt()
        return (from..end).map { index ->
            data.scope(mapOf(name to index)) {
                buildTool.buildAll(
                        children,
                        eventContext,
                        data,
                        upperVisibility,
                        other
                )
            }
        }.flatten()
    }
}