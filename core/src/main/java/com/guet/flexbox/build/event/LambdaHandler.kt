package com.guet.flexbox.build.event

import android.view.View
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.transaction.impl.PageContextImpl

class LambdaHandler(
        pageContext: PageContext,
        private val executable: LambdaExpression
) : EventHandler(pageContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        executable.invoke(args)
        val impl = pageContext as PageContextImpl
        impl.dispatchWithScope(v)
    }
}