package com.guet.flexbox.litho

import com.facebook.litho.EventDispatcher
import com.facebook.litho.HasEventDispatcher
import com.guet.flexbox.build.EventHandler
import com.facebook.litho.EventHandler as BaseEventHandler

class LithoEventHandler<T> private constructor(
        private val target: EventHandler<T>
) : BaseEventHandler<T>(LithoEventDispatcher, 0) {

    override fun dispatchEvent(event: T) = target.invoke(event)

    override fun isEquivalentTo(other: BaseEventHandler<*>?): Boolean {
        return other is LithoEventHandler<*> && target == other.target
    }

    companion object LithoEventDispatcher : HasEventDispatcher, EventDispatcher {

        fun <T> create(handler: EventHandler<T>): LithoEventHandler<T> {
            return LithoEventHandler(handler)
        }

        override fun getEventDispatcher(): EventDispatcher = this

        override fun dispatchOnEvent(eventHandler: com.facebook.litho.EventHandler<in Any?>?, eventState: Any?): Any? {
            eventHandler?.dispatchEvent(eventState)
            return null
        }
    }
}