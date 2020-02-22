package com.guet.flexbox.transaction.impl

import androidx.annotation.CallSuper
import com.guet.flexbox.transaction.ActionKey
import com.guet.flexbox.transaction.Dispatcher
import com.guet.flexbox.transaction.PageTransaction
import com.guet.flexbox.transaction.SendTransaction
import java.util.*

internal open class SendTransactionImpl(
        contextImpl: PageContextImpl
) : PageTransactionImpl(
        contextImpl
), SendTransaction {

    private lateinit var pendingEvents: LinkedList<Array<out Any?>>

    override fun send(vararg values: Any?): SendTransaction {
        if (!this::pendingEvents.isInitialized) {
            pendingEvents = LinkedList()
        }
        pendingEvents.add(values)
        return getWrapper()
    }

    @CallSuper
    override fun dispatch(dispatcher: Dispatcher) {
        super.dispatch(dispatcher)
        if (this::pendingEvents.isInitialized) {
            pendingEvents.forEach {
                dispatcher.dispatchActions(
                        ActionKey.SendObjects,
                        it
                )
            }
        }
    }

    override val outerType: Class<out PageTransaction>
        get() = SendTransaction::class.java
}