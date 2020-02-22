package com.guet.flexbox

import com.guet.flexbox.el.ELProcessor
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.transaction.action.ActionBridge
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
        val impl = ActionBridge().newPageContext()
        p.defineBean("pageContext", impl.newWrapper())
        p.defineBean("xxx","xxx")
        val x2 = p.eval("()->{System.out.println(xxx)}") as LambdaExpression
        x2.invoke()
        val x = p.eval("()->{pageContext.refresh().commit()}") as LambdaExpression
        x.invoke()
        Assert.assertEquals(4, (2 + 2).toLong())
    }
}