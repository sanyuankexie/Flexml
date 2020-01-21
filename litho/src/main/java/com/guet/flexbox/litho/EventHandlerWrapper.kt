package com.guet.flexbox.litho

import com.facebook.litho.ClickEvent
import com.facebook.litho.EventDispatcher
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.build.EventHandler
import com.facebook.litho.EventHandler as BaseEventHandler

internal class EventHandlerWrapper<T>(
        private val target: EventHandler
) : BaseEventHandler<T>(EventHandlerWrapper, 0) {

    override fun dispatchEvent(event: T) {
        when (event) {
            is ClickEvent -> {
                target.invoke(event.view, emptyArray())
            }
            is TextChangedEvent -> {
                target.invoke(event.view, arrayOf(event.text))
            }
        }
    }

    override fun isEquivalentTo(other: BaseEventHandler<*>?): Boolean {
        return other is EventHandlerWrapper && target == other.target
    }

    private companion object : HasEventDispatcher,
            EventDispatcher {

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: BaseEventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}