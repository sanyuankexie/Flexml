package com.guet.flexbox

import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import com.guet.flexbox.transaction.SendTransaction
import org.apache.commons.jexl3.JexlContext

class PageContext(
        private val data: JexlContext,
        private val target: EventTarget
) {

    fun send(vararg values: Any) {
        SendTransaction(data, target)
                .send(*values)
                .commit()
    }

    fun http(): HttpTransaction {
        return HttpTransaction(data, target)
    }

    fun refresh(): RefreshTransaction {
        return RefreshTransaction(data, target)
    }
}