package com.guet.flexbox

import com.guet.flexbox.el.ELProcessor
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect2() {
        val p = ELProcessor()
        p.defineBean("x", emptyList<Any>())
        val x = p.eval("()->{pageContext.refresh().with(()->{clicked=!clicked}).commit()}")
        Assert.assertEquals(4, (2 + 2).toLong())
    }
}