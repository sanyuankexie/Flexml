package com.guet.flexbox

import com.facebook.litho.EventDispatcher
import com.facebook.litho.EventHandler
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.el.LambdaExpression

internal object EventBridge : HasEventDispatcher, EventDispatcher {

    override fun getEventDispatcher(): EventDispatcher = this

    override fun dispatchOnEvent(eventHandler: EventHandler<*>, eventState: Any?): Any? {
        when (eventHandler.id) {
            R.id.on_text_changed -> {
                if (eventHandler is TextChangedEvent) {
                    (eventHandler.params!![0] as LambdaExpression).invoke(eventHandler.text)
                }
            }
            else -> (eventHandler.params!![0] as LambdaExpression).invoke()
        }
        return null
    }

    fun <T> newEventHandler(id: Int, vararg args: Any): EventHandler<T> {
        val a: Array<out Any> = args
        return EventHandler(this, id, a)
    }
}