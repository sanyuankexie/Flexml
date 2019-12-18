package com.guet.flexbox.mock


import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

internal class Looper : Executor {

    private val queue = LinkedBlockingQueue<Runnable>()

    fun loop() {
        while (true) {
            try {
                queue.take().run()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun execute(runnable: Runnable) {
        try {
            queue.put(runnable)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }

    }
}
