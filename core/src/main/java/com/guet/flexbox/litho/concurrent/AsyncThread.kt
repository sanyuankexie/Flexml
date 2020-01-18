package com.guet.flexbox.litho.concurrent

import android.os.Handler
import android.os.HandlerThread
import com.facebook.litho.LithoHandler

internal object AsyncThread : Handler({
    val thread = HandlerThread("Gbox:AsyncThread")
    thread.start()
    thread.looper
}()), LithoHandler {

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