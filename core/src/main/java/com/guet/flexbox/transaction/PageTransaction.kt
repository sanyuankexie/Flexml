package com.guet.flexbox.transaction

import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext

abstract class PageTransaction(
        protected val dataContext: JexlContext,
        protected val eventDispatcher: EventTarget
) {
    abstract fun commit()
}