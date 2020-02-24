package com.guet.flexbox

import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import com.guet.flexbox.transaction.SendTransaction
import org.apache.commons.jexl3.JexlContext

class PageContext(
        private val dataContext: JexlContext,
        private val target: EventTarget
) {

    fun send(vararg values: Any) {
        SendTransaction(dataContext, target)
                .send(*values)
                .commit()
    }

    fun http(): HttpTransaction {
        return HttpTransaction(dataContext, target)
    }

    fun refresh(): RefreshTransaction {
        return RefreshTransaction(dataContext, target)
    }

}