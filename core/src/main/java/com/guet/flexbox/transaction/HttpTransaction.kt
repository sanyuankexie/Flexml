package com.guet.flexbox.transaction

import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import com.guet.flexbox.PageContext
import com.guet.flexbox.event.ActionKey
import com.guet.flexbox.event.HttpAction
import org.apache.commons.jexl3.JexlScript
import org.apache.commons.jexl3.annotations.NoJexl
import java.util.*

class HttpTransaction(
        context: PageContext
) : SendTransaction(context) {

    private var url: String = ""
    private var method: String = "GET"
    private lateinit var prams: ArrayMap<String, String>
    private var success: JexlScript? = null
    private var error: JexlScript? = null

    fun url(url: String): HttpTransaction {
        this.url = url
        return this
    }

    fun method(method: String): HttpTransaction {
        this.method = method.toUpperCase(Locale.UK)
        return this
    }

    fun with(key: String, value: String): HttpTransaction {
        if (!this::prams.isInitialized) {
            prams = ArrayMap()
        }
        prams[key] = value
        return this
    }

    fun error(lambdaExpression: JexlScript): HttpTransaction {
        error = lambdaExpression
        return this
    }

    fun success(lambdaExpression: JexlScript): HttpTransaction {
        success = lambdaExpression
        return this
    }

    @NoJexl
    override fun execute(executor: TransactionExecutor) {
        super.execute(executor)
        val prams = if (this::prams.isInitialized) {
            this.prams
        } else {
            emptyMap<String, String>()
        }
        executor.execute(
                ActionKey.HttpRequest,
                arrayOf(HttpAction(
                        url,
                        method,
                        prams,
                        newCallback(executor)
                ))
        )
    }

    private fun newCallback(executor: TransactionExecutor): HttpAction.Callback {
        return CallbackImpl(
                context,
                executor,
                success,
                error
        )
    }

    private class CallbackImpl(
            private val context: PageContext,
            private val executor: TransactionExecutor,
            private val success: JexlScript?,
            private val error: JexlScript?
    ) : Handler(Looper.getMainLooper()), HttpAction.Callback {

        override fun onError() {
            if (error != null) {
                if (Looper.myLooper() == looper) {
                    executor.execute(
                            ActionKey.ExecuteActions,
                            arrayOf(error)
                    )
                    context.executeTransaction(executor)
                } else {
                    post {
                        executor.execute(
                                ActionKey.ExecuteActions,
                                arrayOf(error)
                        )
                        context.executeTransaction(executor)
                    }
                }
            }
        }

        override fun onResponse(data: String?) {
            if (success != null) {
                if (Looper.myLooper() == looper) {
                    executor.execute(
                            ActionKey.ExecuteActions,
                            arrayOf(success)
                    )
                    context.executeTransaction(executor)
                } else {
                    post {
                        executor.execute(
                                ActionKey.ExecuteActions,
                                arrayOf(success)
                        )
                        context.executeTransaction(executor)
                    }
                }
            }
        }
    }
}