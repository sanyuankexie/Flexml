package com.guet.flexbox.litho

import com.facebook.litho.LithoHandler

internal object LayoutThreadHandler : LithoHandler {

    override fun post(runnable: Runnable, tag: String?) {
        InternalThreads.runOnAsyncThread(runnable)
    }

    override fun postAtFront(runnable: Runnable, tag: String?) {
        throw UnsupportedOperationException()
    }

    override fun isTracing(): Boolean = true

    override fun remove(runnable: Runnable) {
        InternalThreads.removeAsyncCallback(runnable)
    }
}