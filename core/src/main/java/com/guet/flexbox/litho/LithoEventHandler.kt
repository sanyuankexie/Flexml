package com.guet.flexbox.litho

import com.facebook.litho.ClickEvent
import com.facebook.litho.EventDispatcher
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.build.EventHandler
import com.facebook.litho.EventHandler as BaseEventHandler

class LithoEventHandler(
        private val target: EventHandler
) : BaseEventHandler<Any>(LithoEventHandler, 0) {

    override fun dispatchEvent(event: Any) {
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
        return other is LithoEventHandler && target == other.target
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