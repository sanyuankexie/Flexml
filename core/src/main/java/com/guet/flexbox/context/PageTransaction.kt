package com.guet.flexbox.context

import android.os.Handler
import android.os.Looper
import com.guet.flexbox.HttpRequest
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.RefreshPageEvent
import com.guet.flexbox.eventsystem.event.SendObjectsEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.*

class PageTransaction(
        private val dataContext: JexlContext,
        private val eventDispatcher: EventTarget
) {

    private lateinit var httpRequests: LinkedList<HttpRequest>

    private lateinit var pendingSends: LinkedList<Array<out Any?>>

    private lateinit var pendingModifies: LinkedList<JexlScript>

    fun with(l: JexlScript): PageTransaction {
        if (!this::pendingModifies.isInitialized) {
            pendingModifies = LinkedList()
        }
        pendingModifies.add(l)
        return this
    }

    fun send(vararg values: Any?): PageTransaction {
        if (!this::pendingSends.isInitialized) {
            pendingSends = LinkedList()
        }
        pendingSends.add(values)
        return this
    }

    @JvmOverloads
    fun http(
            url: String,
            method: String = "GET",
            body: Map<String, String>,
            onSuccess: JexlScript? = null,
            onError: JexlScript? = null
    ): PageTransaction {
        if (this::httpRequests.isInitialized) {
            httpRequests = LinkedList()
        }
        val httpRequest = HttpRequest(
                url,
                method,
                body,
                newCallback(onSuccess, onError)
        )
        httpRequests.add(httpRequest)
        return this
    }

    fun commit() {
        if (this::pendingSends.isInitialized) {
            pendingSends.forEach {
                eventDispatcher.dispatchEvent(SendObjectsEvent(it))
            }
        }
        if (this::pendingModifies.isInitialized) {
            pendingModifies.forEach {
                it.execute(dataContext)
            }
            eventDispatcher.dispatchEvent(RefreshPageEvent())
        }
    }

    private fun newCallback(
            success: JexlScript?,
            error: JexlScript?
    ): HttpRequest.Callback {
        return CallbackImpl(
                dataContext,
                success,
                error
        )
    }

    private class CallbackImpl(
            private val dataContext: JexlContext,
            private val success: JexlScript?,
            private val error: JexlScript?
    ) : Handler(Looper.getMainLooper()),
            HttpRequest.Callback {

        override fun onError() {
            if (error != null) {
                if (Looper.myLooper() == looper) {
                    error.execute(dataContext)
                } else {
                    post {
                        error.execute(dataContext)
                    }
                }
            }
        }

        override fun onResponse(data: String?) {
            if (success != null) {
                if (Looper.myLooper() == looper) {
                    success.execute(dataContext, data)
                } else {
                    post {
                        success.execute(dataContext, data)
                    }
                }
            }
        }
    }

}