package com.guet.flexbox.eventsystem.event

import android.widget.EditText
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class TextChangedEvent(
        source: EditText,
        text: String?,
        context: JexlContext,
        script: JexlScript
) : ExecutableEvent<EditText>(
        source,
        context,
        script
) {
    val text: String?
        get() = args[0] as? String

    override val args = arrayOf<Any?>(text)
}