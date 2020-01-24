package com.guet.flexbox

import android.os.Handler
import android.os.Looper
import android.os.Process
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory
import java.util.concurrent.ForkJoinWorkerThread
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

object ConcurrentUtils {

    private val mainThreadLooper = Looper.getMainLooper()

    internal val forkJoinPool: ForkJoinPool

    init {
        val count = AtomicInteger(0)
        val nThreads = max(
                Runtime.getRuntime().availableProcessors(),
                4
        )
        forkJoinPool = ForkJoinPool(nThreads,
                ForkJoinWorkerThreadFactory {
                    object : ForkJoinWorkerThread(it) {
                        init {
                            name = "flexbox-pool-${count.getAndIncrement()}"
                        }

                        override fun run() {
                            Process.setThreadPriority(
                                    Process.THREAD_PRIORITY_DEFAULT
                            )
                            super.run()
                        }
                    }
                }, null, true)
    }

    val mainThreadHandler = Handler(mainThreadLooper)

    val threadPool: ExecutorService
        get() = forkJoinPool

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