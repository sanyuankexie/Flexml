package com.guet.flexbox.litho

import com.facebook.litho.*
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.eventsystem.EventAdapter

internal class LithoEventAdapter<T>(
        private val target: EventAdapter
) : EventHandler<T>(EventSystemAdapter, 0) {

    override fun dispatchEvent(event: T) {
        when (event) {
            is ClickEvent -> {
                target.handleEvent(event.view, null)
            }
            is TextChangedEvent -> {
                target.handleEvent(event.view, arrayOf(event.text))
            }
            is VisibleEvent -> {
                target.handleEvent(null, null)
            }
        }
    }

    override fun isEquivalentTo(other: EventHandler<*>?): Boolean {
        return other is LithoEventAdapter && target == other.target
    }

    private companion object EventSystemAdapter : HasEventDispatcher,
            EventDispatcher {

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: EventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}