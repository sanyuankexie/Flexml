package com.guet.flexbox

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

object AppExecutors {

    private val mainThreadLooper = Looper.getMainLooper()

    val threadPool: ThreadPoolExecutor

    init {
        val count = AtomicInteger(0)
        val nThreads = max(
                Runtime.getRuntime().availableProcessors(),
                4
        )
        threadPool = ThreadPoolExecutor(
                nThreads, nThreads,
                3,
                TimeUnit.SECONDS,
                LinkedBlockingQueue<Runnable>(),
                ThreadFactory {
                    Thread(it, "flexbox-pool-${count.getAndIncrement()}")
                }
        )
    }

    val mainThreadHandler = Handler(mainThreadLooper)

    fun runOnUiThread(run: () -> Unit) {
        if (Looper.myLooper() == mainThreadLooper) {
            run()
        } else {
            mainThreadHandler.post(run)
        }
    }

    fun runOnAsyncThread(a: () -> Unit) {
        this.threadPool.execute(a)
    }
}