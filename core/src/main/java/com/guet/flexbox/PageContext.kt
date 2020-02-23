package com.guet.flexbox

import android.view.View
import com.guet.flexbox.event.ActionExecutor
import com.guet.flexbox.event.ActionTarget
import com.guet.flexbox.event.HostEventExecutor
import com.guet.flexbox.transaction.*
import org.apache.commons.jexl3.annotations.NoJexl

class PageContext(
        private val target: ActionTarget
) {

    private val pendingQueue = LinkedHashSet<PageTransaction>()

    fun send(vararg values: Any) {
        SendTransaction(this)
                .send(*values)
                .commit()
    }

    fun http(): HttpTransaction {
        return HttpTransaction(this)
    }

    fun refresh(): RefreshTransaction {
        return RefreshTransaction(this)
    }

    @NoJexl
    fun newHostEventExecutor(v: View?): ActionExecutor {
        return HostEventExecutor(target, v)
    }

    @NoJexl
    fun addToQueue(transaction: PageTransaction) {
        pendingQueue.add(transaction)
    }

    @NoJexl
    fun executeTransaction(executor: ActionExecutor) {
        pendingQueue.forEach {
            it.execute(executor)
        }
        pendingQueue.clear()
    }
}