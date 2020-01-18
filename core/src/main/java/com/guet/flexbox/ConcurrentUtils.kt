package com.guet.flexbox

import android.os.Handler
import android.os.Looper
import androidx.core.math.MathUtils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ConcurrentUtils {

    private val mainThreadLooper = Looper.getMainLooper()

    val mainThreadHandler = Handler(mainThreadLooper)

    val threadPool = kotlin.run {
        val nThreads = MathUtils.clamp(Runtime.getRuntime().availableProcessors(), 2, 4)
        ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue()
        )
    }

    fun runOnUiThread(run: () -> Unit) {
        if (Looper.myLooper() == mainThreadLooper) {
            run()
        } else {
            mainThreadHandler.post(run)
        }
    }

    fun runOnAsyncThread(a: () -> Unit) {
        threadPool.execute(a)
    }
}