package com.guet.flexbox.litho

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val string = ""
        val c1: () -> Unit = {
            print(string)
        }
        val c2: () -> Unit = {
            print(string)
        }
        println(c1.javaClass)
        println(c2.javaClass)
        c1.javaClass.declaredFields.forEach {
            println(it)
        }
        Assert.assertEquals(4, 2 + 2.toLong())
    }
}