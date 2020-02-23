package com.guet.flexbox.transaction

import androidx.annotation.CallSuper
import com.guet.flexbox.PageContext
import org.apache.commons.jexl3.annotations.NoJexl

abstract class PageTransaction(
        protected val context: PageContext
) {

    fun commit() {
        context.addToQueue(this)
    }

    @NoJexl
    @CallSuper
    open fun execute(transactionExecutor: TransactionExecutor) {
    }
}