package com.guet.flexbox.transaction

import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import com.guet.flexbox.HttpRequest
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.ExecuteEvent
import com.guet.flexbox.eventsystem.event.HttpRequestEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.*

class HttpTransaction(
        dataContext: JexlContext,
        eventDispatcher: EventTarget
) : SendTransaction(dataContext, eventDispatcher) {

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

    override fun commit() {
        super.commit()
        eventDispatcher.dispatchEvent(
                HttpRequestEvent(HttpRequest(url, method, prams, newCallback()))
        )
    }

    private fun newCallback(): HttpRequest.Callback {
        return CallbackImpl(
                dataContext,
                eventDispatcher,
                success,
                error
        )
    }

    private class CallbackImpl(
            private val dataContext: JexlContext,
            private val eventDispatcher: EventTarget,
            private val success: JexlScript?,
            private val error: JexlScript?
    ) : Handler(Looper.getMainLooper()), HttpRequest.Callback {

        override fun onError() {
            if (error != null) {
                if (Looper.myLooper() == looper) {
                    eventDispatcher.dispatchEvent(ExecuteEvent(dataContext, error))
                } else {
                    post {
                        eventDispatcher.dispatchEvent(ExecuteEvent(dataContext, error))
                    }
                }
            }
        }

        override fun onResponse(data: String?) {
            if (success != null) {
                if (Looper.myLooper() == looper) {
                    eventDispatcher.dispatchEvent(ExecuteEvent(dataContext, success))
                } else {
                    post {
                        eventDispatcher.dispatchEvent(ExecuteEvent(dataContext, success))
                    }
                }
            }
        }
    }
}