package com.guet.flexbox

import java.lang.ref.WeakReference

internal class EventBridge : EventHandler {

    private var ref: WeakReference<EventHandler>? = null

    internal var target: EventHandler?
        set(value) {
            ref = value?.let { WeakReference(it) }
        }
        get() = ref?.get()

    override fun handleEvent(key: String, value: Any) {
        target?.handleEvent(key, value)
    }
}