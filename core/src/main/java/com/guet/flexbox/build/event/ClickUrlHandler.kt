package com.guet.flexbox.build.event

import android.view.View
import com.guet.flexbox.EventContext
import com.guet.flexbox.EventHandler
import com.guet.flexbox.el.ELContext

internal class ClickUrlHandler(
        elContext: ELContext,
        eventContext: EventContext,
        private val url: String
) : EventHandler(elContext, eventContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        eventContext.toPageContext(v).send(url)
    }
}