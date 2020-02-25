package com.guet.flexbox.eventsystem.event

import java.util.*

abstract class TemplateEvent<T>(
        source: T
) : EventObject(source) {
    override fun getSource(): T {
        @Suppress("UNCHECKED_CAST")
        return super.getSource() as T
    }
}