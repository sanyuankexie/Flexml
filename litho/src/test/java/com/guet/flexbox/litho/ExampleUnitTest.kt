package com.guet.flexbox.litho

import org.junit.Assert
import org.junit.Test
import java.text.NumberFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val v = java.lang.Float.MIN_NORMAL
        println(v<0)
        println(0.00000000000000000000000000000000000000000001f)
        Assert.assertEquals(4, 2 + 2.toLong())
    }
    private fun big(d: Float): String? {
        val nf: NumberFormat = NumberFormat.getInstance()
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.isGroupingUsed = false
        // 结果未做任何处理
        return nf.format(d)
    }
}