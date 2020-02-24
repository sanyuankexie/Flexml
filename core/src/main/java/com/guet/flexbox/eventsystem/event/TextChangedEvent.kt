package com.guet.flexbox.eventsystem.event

import android.widget.EditText
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class TextChangedEvent(
        source: EditText,
        val text: String?,
        context: JexlContext,
        script: JexlScript
) : ConsumableEvent<EditText, Any?>(
        source,
        context,
        script
) {
    override val args: Array<Any?> by lazy { arrayOf<Any?>(text) }
}