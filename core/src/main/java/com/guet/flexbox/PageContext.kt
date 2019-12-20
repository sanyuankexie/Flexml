package com.guet.flexbox

internal class PageContext(
        private val proxy: EventBridge
) {

    @JvmName("send")
    fun send(key: String, vararg data: Any) {
        proxy.handleEvent(key, data)
    }
}