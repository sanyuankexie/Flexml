package com.guet.flexbox.litho

import com.facebook.litho.ClickEvent
import com.facebook.litho.EventDispatcher
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.VisibleEvent
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.build.event.EventHandler
import com.facebook.litho.EventHandler as BaseEventHandler

internal class EventAdapter<T>(
        private val target: EventHandler
) : BaseEventHandler<T>(EventSystemAdapter, 0) {

    override fun dispatchEvent(event: T) {
        when (event) {
            is ClickEvent -> {
                target.handleEvent(event.view, emptyArray())
            }
            is TextChangedEvent -> {
                target.handleEvent(event.view, arrayOf(event.text))
            }
            is VisibleEvent -> {
                target.handleEvent(null, null)
            }
        }
    }

    override fun isEquivalentTo(other: BaseEventHandler<*>?): Boolean {
        return other is EventAdapter && target == other.target
    }

    private companion object EventSystemAdapter : HasEventDispatcher,
            EventDispatcher {

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: BaseEventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}