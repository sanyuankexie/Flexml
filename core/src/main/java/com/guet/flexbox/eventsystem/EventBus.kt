package com.guet.flexbox.eventsystem

import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.TemplateEvent
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

class EventBus : EventTarget {

    private val handlers = HashMap<Class<*>, EventHandler<*>>()

    fun <E : TemplateEvent<*>> subscribe(h: EventHandler<E>): EventHandler<E>? {
        val eventType = getEventType(h)
        if (Modifier.isAbstract(eventType.modifiers)) {
            throw IllegalArgumentException()
        }
        return synchronized(handlers) {
            @Suppress("UNCHECKED_CAST")
            handlers.put(eventType, h) as? EventHandler<E>
        }
    }

    fun unsubscribe(h: EventHandler<*>) {
        val eventType = getEventType(h)
        synchronized(handlers) {
            val oldH = handlers[eventType]
            if (oldH == h) {
                handlers.remove(eventType)
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun dispatchEvent(e: TemplateEvent<*>): Boolean {
        val eventType = e.javaClass
        synchronized(handlers) {
            @Suppress("UNCHECKED_CAST")
            return (handlers[eventType] as? EventHandler<TemplateEvent<*>>)
                    ?.handleEvent(e) ?: false
        }
    }

    private companion object {
        private fun getEventType(h: EventHandler<*>): Class<*> {
            return try {
                (h.javaClass.genericInterfaces.find {
                    (it as? ParameterizedType)?.rawType == EventHandler::class.java
                } as ParameterizedType).actualTypeArguments[0] as Class<*>
            } catch (e: Throwable) {
                throw IllegalArgumentException(e)
            }
        }
    }
}