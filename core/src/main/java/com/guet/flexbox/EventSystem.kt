package com.guet.flexbox

import com.facebook.litho.ClickEvent
import com.facebook.litho.EventDispatcher
import com.facebook.litho.EventHandler
import com.facebook.litho.HasEventDispatcher
import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.el.LambdaExpression

internal object EventSystem : HasEventDispatcher, EventDispatcher {

    override fun getEventDispatcher(): EventDispatcher = this

    private operator fun <T> Array<Any>?.get(index: Int, type: Class<T>): T? {
        return type.cast(this?.get(index))
    }

    override fun dispatchOnEvent(eventHandler: EventHandler<*>, eventState: Any?): Any? {
        when (eventHandler.id) {
            R.id.on_text_changed -> {
                if (eventState is TextChangedEvent) {
                    eventHandler.params[0, LambdaExpression::class.java]?.invoke(
                            eventState.view,
                            eventState.text
                    )
                }
            }
            R.id.on_click -> {
                if (eventState is ClickEvent) {
                    eventHandler.params[0, LambdaExpression::class.java]?.invoke(
                            eventState.view
                    )
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