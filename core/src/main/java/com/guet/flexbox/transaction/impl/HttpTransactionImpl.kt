package com.guet.flexbox.transaction.impl

import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.transaction.ActionKey
import com.guet.flexbox.transaction.Dispatcher
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.PageTransaction
import com.guet.flexbox.transaction.action.HttpAction

internal class HttpTransactionImpl(
        contextImpl: PageContextImpl
) : SendTransactionImpl(contextImpl), HttpTransaction {

    private var url: String = ""
    private var method: String = "get"
    private lateinit var prams: ArrayMap<String, String>
    private var success: LambdaExpression? = null
    private var error: LambdaExpression? = null

    override val outerType: Class<out PageTransaction>
        get() = HttpTransaction::class.java

    override fun send(vararg values: Any?): HttpTransaction {
        super.send(*values)
        return getWrapper()
    }

    override fun url(url: String): HttpTransaction {
        this.url = url
        return getWrapper()
    }

    override fun method(method: String): HttpTransaction {
        this.method = method
        return getWrapper()
    }

    override fun with(key: String, value: String): HttpTransaction {
        if (!this::prams.isInitialized) {
            prams = ArrayMap()
        }
        prams[key] = value
        return getWrapper()
    }

    override fun error(lambdaExpression: LambdaExpression): HttpTransaction {
        success = lambdaExpression
        return getWrapper()
    }

    override fun success(lambdaExpression: LambdaExpression): HttpTransaction {
        error = lambdaExpression
        return getWrapper()
    }

    override fun dispatch(dispatcher: Dispatcher) {
        super.dispatch(dispatcher)
        val prams = if (this::prams.isInitialized) {
            this.prams
        } else {
            emptyMap<String, String>()
        }
        dispatcher.dispatchActions(
                ActionKey.HttpRequest,
                arrayOf(HttpAction(
                        url,
                        method,
                        prams,
                        newCallback(dispatcher)
                ))
        )
    }

    private fun newCallback(dispatcher: Dispatcher): HttpAction.Callback {
        return CallbackImpl(
                contextImpl,
                dispatcher,
                success,
                error
        )
    }

    private class CallbackImpl(
            private val contextImpl: PageContextImpl,
            private val dispatcher: Dispatcher,
            private val success: LambdaExpression?,
            private val error: LambdaExpression?
    ) : Handler(Looper.getMainLooper()), HttpAction.Callback {

        override fun onError() {
            if (error != null) {
                if (Looper.myLooper() == looper) {
                    error.invoke()
                    contextImpl.dispatchQueue(dispatcher)
                } else {
                    post {
                        error.invoke()
                        contextImpl.dispatchQueue(dispatcher)
                    }
                }
            }
        }

        override fun onResponse(data: String?) {
            if (success != null) {
                if (Looper.myLooper() == looper) {
                    success.invoke()
                    contextImpl.dispatchQueue(dispatcher)
                } else {
                    post {
                        success.invoke()
                        contextImpl.dispatchQueue(dispatcher)
                    }
                }
            }
        }
    }
}