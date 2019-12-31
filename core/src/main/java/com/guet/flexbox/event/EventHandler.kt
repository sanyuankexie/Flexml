package com.guet.flexbox.event

import com.facebook.litho.EventHandler

abstract class EventHandler<T> : EventHandler<T>(EventDispatcher, 0) {

    abstract override fun dispatchEvent(event: T)

    override fun isEquivalentTo(other: EventHandler<*>?): Boolean {
        return other == this
    }
}