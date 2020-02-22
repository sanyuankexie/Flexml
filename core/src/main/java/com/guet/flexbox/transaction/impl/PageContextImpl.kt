package com.guet.flexbox.transaction.impl

import android.view.View
import com.guet.flexbox.transaction.Dispatcher
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.transaction.RefreshTransaction
import com.guet.flexbox.transaction.action.ActionTarget
import java.lang.reflect.Proxy

internal class PageContextImpl(
        private val actionTarget: ActionTarget
) : PageContext {

    private val pendingQueue = LinkedHashSet<PageTransactionImpl>()

    fun pendingToQueue(transaction: PageTransactionImpl) {
        pendingQueue.add(transaction)
    }

    override fun send(vararg values: Any) {
        SendTransactionImpl(this)
                .send(*values)
                .commit()
    }

    override fun http(): HttpTransaction {
        return HttpTransactionImpl(this)
    }

    override fun refresh(): RefreshTransaction {
        return RefreshTransactionImpl(this)
    }

    fun newWrapper(): PageContext {
        return Proxy.newProxyInstance(
                PageContext::class.java.classLoader,
                arrayOf(PageContext::class.java)
        ) { proxy, method, args ->
            when {
                method.declaringClass == Any::class.java -> {
                    method.invoke(proxy, args)
                }
                args.isNullOrEmpty() -> {
                    method.invoke(this@PageContextImpl)
                }
                else -> {
                    method.invoke(this@PageContextImpl, *args)
                }
            }
        } as PageContext
    }

    fun dispatchWithScope(view: View?) {
        dispatchQueue(ViewScopeDispatcher(actionTarget, view))
    }

    fun dispatchQueue(dispatcher: Dispatcher) {
        pendingQueue.forEach {
            it.dispatch(dispatcher)
        }
        pendingQueue.clear()
    }
}