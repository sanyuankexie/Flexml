package com.guet.flexbox.eventsystem

import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.TemplateEvent
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

class EventBus : EventTarget {

    private val handlers = ArrayList<EventHandler<*>>()

    fun <E : TemplateEvent<*, *>> subscribe(h: EventHandler<E>) {
        val eventType = getEventType(h)
        if (Modifier.isAbstract(eventType.modifiers)) {
            throw IllegalArgumentException()
        }
        synchronized(handlers) {
            handlers.add(h)
        }
    }

    fun unsubscribe(h: EventHandler<*>) {
        synchronized(handlers) {
            handlers.remove(h)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun dispatchEvent(e: TemplateEvent<*, *>) {
        synchronized(handlers) {
            handlers.forEach {
                @Suppress("UNCHECKED_CAST")
                (it as EventHandler<TemplateEvent<*, *>>).handle(e)
            }
        }
    }

    private companion object {
        private fun getEventType(h: EventHandler<*>): Class<*> {
            return try {
                val type = h.javaClass
                        .genericSuperclass as ParameterizedType
                type.actualTypeArguments[0] as Class<*>
            } catch (e: Throwable) {
                throw IllegalArgumentException(e)
            }
        }
    }
}