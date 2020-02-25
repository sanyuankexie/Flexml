package com.guet.flexbox.eventsystem

import android.util.Log
import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.TemplateEvent
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class EventDispatcher : EventTarget {

    private var targetRef: WeakReference<EventTarget>? = null

    var target: EventTarget?
        set(value) {
            targetRef = if (value != null) {
                val t = target
                if (t != null && t != value) {
                    Log.e("EventDispatcher",
                            "This Page is set to two HostingView. " +
                                    "This is not support.")
                }
                WeakReference(value)
            } else {
                null
            }
        }
        get() {
            return targetRef?.get()
        }

    override fun dispatchEvent(e: TemplateEvent<*>): Boolean {
        val result = target?.dispatchEvent(e) ?: false
        if (!result && e is Callable<*>) {
            e.call()
            return true
        }
        return false
    }
}