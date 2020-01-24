package com.guet.flexbox

import android.os.Handler
import android.os.Looper
import android.os.Process
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory
import java.util.concurrent.ForkJoinWorkerThread
import java.util.concurrent.atomic.AtomicInteger

object ConcurrentUtils {

    private val mainThreadLooper = Looper.getMainLooper()

    private fun clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    internal val forkJoinPool: ForkJoinPool

    init {
        val count = AtomicInteger(0)
        val nThreads = clamp(
                Runtime.getRuntime().availableProcessors(),
                2, 4
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
        }, null, false)
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