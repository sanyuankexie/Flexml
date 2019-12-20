package com.guet.flexbox.content

import com.guet.flexbox.EventHandler

class RenderContent internal constructor(
        private val context: PageContext,
        internal val root: RenderNode
) {
    fun setEventListener(value: EventHandler) {
        context.handler = value
    }
}