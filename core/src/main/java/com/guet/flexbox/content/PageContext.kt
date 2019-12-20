package com.guet.flexbox.content

import com.guet.flexbox.EventHandler

internal class PageContext @JvmOverloads constructor(
        var handler: EventHandler? = null
) {

    @JvmName("send")
    fun send(key: String, vararg data: Any) {
        handler?.handleEvent(key, data)
    }
}