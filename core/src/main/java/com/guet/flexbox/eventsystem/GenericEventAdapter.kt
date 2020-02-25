package com.guet.flexbox.eventsystem

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class GenericEventAdapter(
        private val factory: EventFactory,
        private val dataContext: JexlContext,
        private val eventDispatcher: EventTarget,
        private val script: JexlScript
) : EventAdapter {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        eventDispatcher.dispatchEvent(factory
                .create(v, args, dataContext, script)
        )
    }
}