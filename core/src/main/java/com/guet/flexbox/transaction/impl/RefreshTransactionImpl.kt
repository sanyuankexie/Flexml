package com.guet.flexbox.transaction.impl

import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.transaction.dispatch.ActionKey
import com.guet.flexbox.transaction.dispatch.Dispatcher
import com.guet.flexbox.transaction.PageTransaction
import com.guet.flexbox.transaction.RefreshTransaction
import java.util.*

internal class RefreshTransactionImpl(
        contextImpl: PageContextImpl
) : SendTransactionImpl(contextImpl), RefreshTransaction {

    private lateinit var paddingModify: LinkedList<LambdaExpression>

    override fun with(l: LambdaExpression): RefreshTransaction {
        if (!this::paddingModify.isInitialized) {
            paddingModify = LinkedList()
        }
        paddingModify.add(l)
        return getWrapper()
    }

    override fun send(vararg values: Any?): RefreshTransaction {
        super.send(*values)
        return getWrapper()
    }

    override fun dispatch(dispatcher: Dispatcher) {
        super.dispatch(dispatcher)
        if (this::paddingModify.isInitialized) {
            paddingModify.forEach {
                it.invoke()
            }
            paddingModify.clear()
            dispatcher.dispatchAction(
                    ActionKey.RefreshPage
            )
        } else {
            dispatcher.dispatchAction(
                    ActionKey.RefreshPage
            )
        }

    }

    override val outerType: Class<out PageTransaction>
        get() = RefreshTransaction::class.java
}