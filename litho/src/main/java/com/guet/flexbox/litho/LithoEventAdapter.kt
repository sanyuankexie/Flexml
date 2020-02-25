package com.guet.flexbox.litho

import com.facebook.litho.*
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.eventsystem.EventAdapter

internal class LithoEventAdapter<T>(
        private val target: EventAdapter
) : EventHandler<T>(LithoEventAdapter, 0) {

    override fun dispatchEvent(event: T) {
        when (event) {
            is ClickEvent -> {
                target.adapt(event.view, null)
            }
            is TextChangedEvent -> {
                target.adapt(event.view, arrayOf(event.text))
            }
            is VisibleEvent -> {
                target.adapt(null, null)
            }
        }
    }

    override fun isEquivalentTo(other: EventHandler<*>?): Boolean {
        return other is LithoEventAdapter && target == other.target
    }

    private companion object : HasEventDispatcher,
            EventDispatcher {

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: EventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}