package com.guet.flexbox

import java.lang.ref.WeakReference

class WeakEventHandler private constructor(
        referent: EventHandler
) : WeakReference<EventHandler>(referent), EventHandler {

    override fun handleEvent(key: String, value: Array<out Any>) {
        get()?.handleEvent(key, value)
    }

    companion object {
        @JvmStatic
        fun toWeak(ref: EventHandler): EventHandler {
            return WeakEventHandler(ref)
        }
    }
}