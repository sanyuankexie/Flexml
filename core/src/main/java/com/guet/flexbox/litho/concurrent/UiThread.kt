package com.guet.flexbox.litho.concurrent

import android.os.Handler
import android.os.Looper
import com.guet.flexbox.litho.concurrent.UiThread.post

internal object UiThread : Handler(Looper.getMainLooper()) {
    fun runOnUiThread(run: () -> Unit) {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            run()
        } else {
            post(run)
        }
    }
}