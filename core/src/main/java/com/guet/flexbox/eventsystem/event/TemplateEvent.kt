package com.guet.flexbox.eventsystem.event

import java.util.*

abstract class TemplateEvent<T, V>(
        source: T
) : EventObject(source) {

    abstract val value: V?

    override fun getSource(): T {
        @Suppress("UNCHECKED_CAST")
        return super.getSource() as T
    }
}