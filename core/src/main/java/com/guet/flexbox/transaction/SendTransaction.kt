package com.guet.flexbox.transaction

import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.SendObjectsEvent
import org.apache.commons.jexl3.JexlContext
import java.util.*

open class SendTransaction(
        dataContext: JexlContext,
        eventDispatcher: EventTarget
) : PageTransaction(dataContext, eventDispatcher) {

    private lateinit var pendingSends: LinkedList<Array<out Any?>>

    fun send(vararg values: Any?): PageTransaction {
        if (!this::pendingSends.isInitialized) {
            pendingSends = LinkedList()
        }
        pendingSends.add(values)
        return this
    }

    override fun commit() {
        if (this::pendingSends.isInitialized) {
            pendingSends.forEach {
                eventDispatcher.dispatchEvent(SendObjectsEvent(it))
            }
        }
    }
}