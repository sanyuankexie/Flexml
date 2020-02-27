package com.guet.flexbox.eventsystem.event

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.concurrent.Callable

class VisibleEvent(
        context: JexlContext,
        script: JexlScript
) : TemplateEvent<Unit>(Unit), HasExprEvent {
    override val expr: Callable<Any> = script.callable(context)
    companion object Factory : TemplateEvent.Factory {
        override fun create(
                source: View?,
                args: Array<out Any?>?,
                dataContext: JexlContext,
                script: JexlScript
        ): TemplateEvent<*> {
            return VisibleEvent(dataContext, script)
        }
    }
}