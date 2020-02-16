package com.guet.flexbox.litho

import com.facebook.litho.LithoHandler
import com.guet.flexbox.AppExecutors

internal object LayoutThreadHandler : LithoHandler {

    override fun post(runnable: Runnable, tag: String?) {
        AppExecutors.runOnAsyncThread(runnable)
    }

    override fun postAtFront(runnable: Runnable, tag: String?) {
        throw UnsupportedOperationException()
    }

    override fun isTracing(): Boolean = true

    override fun remove(runnable: Runnable) {
        AppExecutors.removeAsyncCallback(runnable)
    }
}