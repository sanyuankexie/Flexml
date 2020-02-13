package com.guet.flexbox.litho

import org.junit.Assert
import org.junit.Test
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    private val queue = ReferenceQueue<Any>()

    private val weak = WeakReference<Any>(Any(),queue)

    @Test
    fun addition_isCorrect() {
        System.gc()
        val ref= queue.remove(0)
        println(ref.get())
        Assert.assertEquals(4, 2 + 2.toLong())
    }

    fun test() {

    }
}