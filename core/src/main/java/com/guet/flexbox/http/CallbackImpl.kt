package com.guet.flexbox.http

import android.os.Handler
import android.os.Looper
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

internal class CallbackImpl(
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

    private companion object {
        private val mainLooper = Looper.getMainLooper()
        private val mainThread = Handler(mainLooper)
    }
}