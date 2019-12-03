package com.luke.skywalker

internal class EventSender(private val listener: EventListener?) {

    @JvmName("send")
    fun send(key: String) {
        sendWithData(key)
    }

    @JvmName("sendWithData")
    fun sendWithData(key: String, vararg data: Any) {
        listener?.handleEvent(key, data)
    }
}