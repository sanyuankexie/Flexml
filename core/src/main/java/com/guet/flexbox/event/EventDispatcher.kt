package com.guet.flexbox.event

import com.facebook.litho.EventDispatcher
import com.facebook.litho.EventHandler
import com.facebook.litho.HasEventDispatcher

internal object EventDispatcher : HasEventDispatcher, EventDispatcher {

    override fun getEventDispatcher(): EventDispatcher = this

    override fun dispatchOnEvent(eventHandler: EventHandler<in Any?>?, eventState: Any?): Any? {
        eventHandler?.dispatchEvent(eventState)
        return null
    }
}