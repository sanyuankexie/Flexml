package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.context.ScopeContext
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine
import java.util.*

object For : Declaration() {

    override val dataBinding by DataBinding
            .create {
                text("var")
                value("from")
                value("to")
            }

    override fun onBuildWidget(
            buildTool: BuildTool,
            rawAttrs: Map<String, String>,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperDisplay: Boolean
    ): List<Any> {
        val attrs = bindAttrs(rawAttrs, engine, dataContext, eventDispatcher)
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Float).toInt()
        val to = (attrs.getValue("to") as Float).toInt()
        val list = LinkedList<Any>()
        for (index in from..to) {
            val scope = ScopeContext(mapOf(name to index), dataContext)
            val subList = buildTool.buildAll(
                    children,
                    engine,
                    scope,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
            list.addAll(subList)
        }
        return list
    }
}