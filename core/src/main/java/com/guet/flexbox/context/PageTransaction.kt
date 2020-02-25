package com.guet.flexbox.context

import android.os.Handler
import android.os.Looper
import com.guet.flexbox.HttpRequest
import com.guet.flexbox.eventsystem.EventTarget
import com.guet.flexbox.eventsystem.event.HttpRequestEvent
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
        if (this::httpRequests.isInitialized) {
            httpRequests.forEach {
                eventDispatcher.dispatchEvent(HttpRequestEvent(it))
            }
        }
    }

    private companion object {
        private val mainLooper = Looper.getMainLooper()
        private val mainThread = Handler(Looper.getMainLooper())
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
    ) : HttpRequest.Callback {

        override fun onError() {
            if (error != null) {
                if (Looper.myLooper() == mainLooper) {
                    error.execute(dataContext)
                } else {
                    mainThread.post {
                        error.execute(dataContext)
                    }
                }
            }
        }

        override fun onResponse(data: String?) {
            if (success != null) {
                if (Looper.myLooper() == mainLooper) {
                    success.execute(dataContext, data)
                } else {
                    mainThread.post {
                        success.execute(dataContext, data)
                    }
                }
            }
        }
    }

}