package com.guet.flexbox.litho

import com.facebook.litho.LithoHandler
import com.guet.flexbox.ConcurrentUtils

internal object LayoutThreadHandler : LithoHandler {

    override fun post(runnable: Runnable, tag: String?) {
        ConcurrentUtils.threadPool.execute(runnable)
    }

    override fun postAtFront(runnable: Runnable, tag: String?) {
        throw UnsupportedOperationException()
    }

    override fun isTracing(): Boolean = true

    override fun remove(runnable: Runnable) {
        ConcurrentUtils.threadPool.remove(runnable)
    }
}