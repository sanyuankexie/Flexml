package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventDispatcher
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext

class ToWidget(
        private val declaration: Declaration,
        private val factory: RenderNodeFactory<*>?
) {
    fun toWidget(
            bindings: BuildTool,
            template: TemplateNode,
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
                dataContext,
                eventDispatcher,
                other,
                upperVisibility
        )
    }
}