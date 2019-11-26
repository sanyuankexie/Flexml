package com.guet.flexbox.build

import com.guet.flexbox.EventListener

internal class EventBus(private val eventListener: EventListener?) {
    fun sendEvent(key: String, vararg values: Any) {
        eventListener?.handleEvent(key, values)
    }
}