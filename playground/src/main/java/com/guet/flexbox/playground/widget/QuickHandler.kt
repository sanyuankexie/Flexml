package com.guet.flexbox.playground.widget

import android.os.Handler
import android.os.HandlerThread

open class QuickHandler(name: String?) : Handler({
    val handlerThread = HandlerThread(name)
    handlerThread.start()
    handlerThread.looper
}())