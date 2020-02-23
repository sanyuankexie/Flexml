package com.guet.flexbox.event

import android.view.View
import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.transaction.impl.PageContextImpl

internal class ClickUrlHandler(
        pageContext: PageContext,
        private val url: String
) : EventHandler(pageContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        pageContext.send(url)
        val impl = pageContext as PageContextImpl
        impl.dispatchWithScope(v)
    }
}