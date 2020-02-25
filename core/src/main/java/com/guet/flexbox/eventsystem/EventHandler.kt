package com.guet.flexbox.eventsystem

import com.guet.flexbox.eventsystem.event.TemplateEvent
import java.util.*

interface EventHandler<in E : TemplateEvent<*>> : EventListener {
    fun handleEvent(e: E): Boolean
}