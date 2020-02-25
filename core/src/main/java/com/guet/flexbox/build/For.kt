package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

object For : Declaration() {

    override val dataBinding by DataBinding
            .create {
                text("var")
                value("from")
                value("to")
            }

    override fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperVisibility: Boolean
    ): List<Any> {
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Float).toInt()
        val end = (attrs.getValue("to") as Float).toInt()
        return (from..end).map { index ->
            val scope = ScopeContext(mapOf(name to index), dataContext)
            buildTool.buildAll(
                    children,
                    engine,
                    scope,
                    eventDispatcher,
                    other,
                    upperVisibility
            )
        }.flatten()
    }
}