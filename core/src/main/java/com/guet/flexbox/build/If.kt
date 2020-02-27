package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

object If : Declaration() {

    override val dataBinding by DataBinding
            .create {
                bool("test")
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
        return if (attrs.getValue("test") as? Boolean != true) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    engine,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperDisplay
            )
        }
    }
}