package com.guet.flexbox.event

import android.view.View
import com.guet.flexbox.PageContext

internal class ClickUrlHandler(
        private val pageContext: PageContext,
        private val url: String
) : EventHandler {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        pageContext.send(url)
        pageContext.dispatchWithViewScope(v)
    }
}