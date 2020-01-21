package com.guet.flexbox

import android.os.Handler
import android.os.Looper
import android.os.Process
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object ConcurrentUtils {

    private val count = AtomicInteger(0)

    private val mainThreadLooper = Looper.getMainLooper()

    val mainThreadHandler = Handler(mainThreadLooper)

    private fun clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    val threadPool = kotlin.run {
        val nThreads = clamp(
                Runtime.getRuntime().availableProcessors(),
                1, 1
        )
        ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                ThreadFactory {
                    Thread {
                        Thread.currentThread().name = "flexbox-${count.getAndIncrement()}"
                        Process.setThreadPriority(
                                Process.THREAD_PRIORITY_DEFAULT
                        )
                        it.run()
                    }
                }
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