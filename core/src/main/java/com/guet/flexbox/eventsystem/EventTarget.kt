package com.guet.flexbox.eventsystem

import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.TemplateEvent

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface EventTarget {
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun dispatchEvent(e: TemplateEvent<*, *>)
}