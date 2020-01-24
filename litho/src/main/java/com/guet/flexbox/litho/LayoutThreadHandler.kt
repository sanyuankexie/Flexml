package com.guet.flexbox.litho

import android.os.Handler
import android.os.HandlerThread
import com.facebook.litho.LithoHandler

internal object LayoutThreadHandler : Handler(
        HandlerThread("flexbox-layout").apply {
            start()
        }.looper
), LithoHandler {

    override fun post(runnable: Runnable, tag: String?) {
        post(runnable)
    }

    override fun postAtFront(runnable: Runnable, tag: String?) {
        postAtFrontOfQueue(runnable)
    }

    override fun isTracing(): Boolean = true

    override fun remove(runnable: Runnable) {
        removeCallbacks(runnable)
    }
}