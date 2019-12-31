package com.guet.flexbox.event

import com.facebook.litho.ClickEvent
import com.guet.flexbox.PageContext

class ClickUrlEventHandler(
        private val pageContext: PageContext,
        private val url: String
) : AbstractEventHandler<ClickEvent>() {

    override fun dispatchEvent(event: ClickEvent) {
        pageContext.send(url)
    }
}