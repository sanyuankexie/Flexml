package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

class ToWidget(
        private val declaration: Declaration,
        private val factory: RenderNodeFactory<*>?
) {
    fun toWidget(
            bindings: BuildTool,
            template: TemplateNode,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperVisibility: Boolean = true
    ): List<Any> {
        return declaration.transform(
                bindings,
                template.attrs ?: emptyMap(),
                template.children ?: emptyList(),
                factory,
                engine,
                dataContext,
                eventDispatcher,
                other,
                upperVisibility
        )
    }
}