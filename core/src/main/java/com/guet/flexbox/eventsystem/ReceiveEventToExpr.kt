package com.guet.flexbox.eventsystem

import android.view.View
import com.guet.flexbox.eventsystem.event.TemplateEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

internal class ReceiveEventToExpr(
        private val factory: TemplateEvent.Factory,
        private val dataContext: JexlContext,
        private val eventDispatcher: EventTarget,
        private val script: JexlScript
) : ExternalEventReceiver {
    override fun receive(v: View?, args: Array<out Any?>?) {
        eventDispatcher.dispatchEvent(factory
                .create(v, args, dataContext, script)
        )
    }
}