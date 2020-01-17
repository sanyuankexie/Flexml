package com.guet.flexbox.litho

import android.view.View
import com.facebook.litho.ClickEvent
import com.facebook.litho.EventDispatcher
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.build.EventHandler
import com.guet.flexbox.build.EventHandlerFactory
import com.facebook.litho.EventHandler as BaseEventHandler

class LithoEventHandler private constructor(
        private val target: EventHandler
) : BaseEventHandler<Any>(Factory, 0) {

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

    companion object Factory : HasEventDispatcher,
            EventDispatcher,
            EventHandlerFactory {

        override fun create(a: (View, Array<out Any?>) -> Unit): Any {
            return LithoEventHandler(a)
        }

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: com.facebook.litho.EventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}