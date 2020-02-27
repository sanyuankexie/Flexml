package com.guet.flexbox.eventsystem.event

import android.view.View
import android.widget.EditText
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.concurrent.Callable

class TextChangedEvent(
        source: EditText,
        val text: String?,
        context: JexlContext,
        script: JexlScript
) : TemplateEvent<EditText>(
        source
), HasExprEvent {
    override val expr: Callable<Any> = script.callable(context)

    companion object Factory : TemplateEvent.Factory {
        override fun create(
                source: View?,
                args: Array<out Any?>?,
                dataContext: JexlContext,
                script: JexlScript
        ): TemplateEvent<*> {
            return TextChangedEvent(
                    source as EditText,
                    args?.get(0) as? String,
                    dataContext,
                    script
            )
        }
    }
}