package com.guet.flexbox.eventsystem

import android.view.View
import com.guet.flexbox.eventsystem.event.TemplateEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

interface EventFactory {
    fun create(
            source: View?,
            args: Array<out Any?>?,
            dataContext: JexlContext,
            script: JexlScript
    ): TemplateEvent<*>
}