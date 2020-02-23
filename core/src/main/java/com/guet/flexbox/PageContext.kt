package com.guet.flexbox

import android.view.View
import com.guet.flexbox.event.ActionTarget
import com.guet.flexbox.transaction.*
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.annotations.NoJexl

class PageContext(
        private val actionTarget: ActionTarget
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
    fun addToQueue(transaction: PageTransaction) {
        pendingQueue.add(transaction)
    }

    @NoJexl
    fun executeTransaction(scope: View?, dataContext: JexlContext) {
        executeTransaction(HostEventExecutor(actionTarget, scope, dataContext))
    }

    @NoJexl
    fun executeTransaction(executor: TransactionExecutor) {
        pendingQueue.forEach {
            it.execute(executor)
        }
        pendingQueue.clear()
    }
}