package com.guet.flexbox.transaction

import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.ExecuteEvent
import com.guet.flexbox.eventsystem.event.RefreshPageEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.*

class RefreshTransaction(
        dataContext: JexlContext,
        eventDispatcher: EventTarget
) : SendTransaction(dataContext, eventDispatcher) {

    private lateinit var paddingModify: LinkedList<JexlScript>

    fun with(l: JexlScript): RefreshTransaction {
        if (!this::paddingModify.isInitialized) {
            paddingModify = LinkedList()
        }
        paddingModify.add(l)
        return this
    }

    override fun commit() {
        super.commit()
        if (this::paddingModify.isInitialized) {
            paddingModify.forEach {
                eventDispatcher.dispatchEvent(ExecuteEvent(dataContext, it))
            }
            eventDispatcher.dispatchEvent(RefreshPageEvent())
        }
    }
}