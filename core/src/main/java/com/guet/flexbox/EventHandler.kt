package com.guet.flexbox

import android.view.View
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.unWrap

abstract class EventHandler(
        elContext: ELContext,
        protected val hostContext: HostContext
) {

    protected val elContext: ELContext = elContext.unWrap()

    abstract fun handleEvent(v: View?, args: Array<out Any?>?)
}