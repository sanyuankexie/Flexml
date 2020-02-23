package com.guet.flexbox.event

import android.view.View
import com.guet.flexbox.transaction.PageContext

abstract class EventHandler(
        protected val pageContext: PageContext
) {

    abstract fun handleEvent(v: View?, args: Array<out Any?>?)
}