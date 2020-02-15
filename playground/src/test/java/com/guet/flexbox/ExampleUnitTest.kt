package com.guet.flexbox

import org.junit.Assert
import org.junit.Test
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        println(findHostAddress())
        Assert.assertEquals(4, 2 + 2.toLong())
    }


    fun findHostAddress(): String? {

        return null
    }
}