package com.guet.flexbox.litho

import com.facebook.litho.*
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.eventsystem.ExternalEventReceiver

internal class LithoEventHandler<T>(
        private val target: ExternalEventReceiver
) : EventHandler<T>(LithoEventHandler, 0) {

    override fun dispatchEvent(event: T) {
        when (event) {
            is ClickEvent -> {
                target.receive(event.view, null)
            }
            is TextChangedEvent -> {
                target.receive(event.view, arrayOf(event.text))
            }
            is VisibleEvent -> {
                target.receive(null, null)
            }
        }
    }

    override fun isEquivalentTo(other: EventHandler<*>?): Boolean {
        return other is LithoEventHandler && target == other.target
    }

    private companion object : HasEventDispatcher, EventDispatcher {

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: EventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}