package com.guet.flexbox.eventsystem.event

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
}