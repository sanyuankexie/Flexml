package com.guet.flexbox.build.event

import android.view.View
import com.guet.flexbox.EventHandler
import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext

internal class ClickUrlHandler(
        elContext: ELContext,
        hostContext: HostContext,
        private val url: String
) : EventHandler(elContext, hostContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        hostContext.toPageContext(v).send(args)
    }
}