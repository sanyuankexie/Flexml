package com.guet.flexbox.transaction.impl

import androidx.annotation.CallSuper
import com.guet.flexbox.transaction.Dispatcher
import com.guet.flexbox.transaction.PageTransaction
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

internal abstract class PageTransactionImpl(
        protected val contextImpl: PageContextImpl
) : PageTransaction {

    final override fun commit() {
        contextImpl.pendingToQueue(this)
    }

    protected abstract val outerType: Class<out PageTransaction>

    private val wrapperInstance by lazy(WrapperLoader())

    protected fun <T> getWrapper(): T {
        @Suppress("UNCHECKED_CAST")
        return wrapperInstance as T
    }

    private inner class WrapperLoader : () -> Any, InvocationHandler {

        override fun invoke(): Any {
            return Proxy.newProxyInstance(
                    outerType.classLoader,
                    arrayOf(outerType), this
            )
        }

        override fun invoke(
                proxy: Any,
                method: Method,
                args: Array<out Any>?
        ): Any? {
            return when {
                method.declaringClass == Any::class.java -> {
                    method.invoke(proxy, args)
                }
                args.isNullOrEmpty() -> {
                    method.invoke(this@PageTransactionImpl)
                }
                else -> {
                    method.invoke(this@PageTransactionImpl, *args)
                }
            }
        }
    }

    @CallSuper
    open fun dispatch(dispatcher: Dispatcher) {
    }
}