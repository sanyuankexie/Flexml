package com.guet.flexbox

internal class EventSender(private val listener: EventListener?) {

    @JvmName("send")
    fun send(key: String, vararg data: Any) {
        listener?.handleEvent(key, data)
    }
}