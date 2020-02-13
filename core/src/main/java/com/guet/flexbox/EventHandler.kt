package com.guet.flexbox

import android.view.View
import com.guet.flexbox.el.ELContext

abstract class EventHandler(
        protected val elContext: ELContext,
        protected val eventContext: EventContext
) {

    abstract fun handleEvent(v: View?, args: Array<out Any?>?)
}