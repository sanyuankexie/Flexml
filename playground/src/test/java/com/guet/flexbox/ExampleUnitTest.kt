package com.guet.flexbox

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .build()
        val o = okHttpClient.newCall(Request.Builder()
                .url("http://localhost:8080/qrcode")
                .get()
                .build())
                .execute()
        println(o)
        Assert.assertEquals(4, 2 + 2.toLong())
    }


    fun findHostAddress(): String? {

        return null
    }
}