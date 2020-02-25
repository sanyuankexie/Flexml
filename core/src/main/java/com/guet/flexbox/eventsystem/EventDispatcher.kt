package com.guet.flexbox.eventsystem

import android.util.Log
import androidx.annotation.RestrictTo
import com.guet.flexbox.eventsystem.event.ExecutableEvent
import com.guet.flexbox.eventsystem.event.TemplateEvent
import java.lang.ref.WeakReference

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class EventDispatcher : EventTarget {

    private var targetImpl: WeakReference<EventTarget>? = null

    var target: EventTarget?
        set(value) {
            targetImpl = if (value != null) {
                val t = target
                if (t != null && t != value) {
                    Log.e("EventBridge",
                            "This Page is set to two HostingView. " +
                                    "This is not support.")
                }
                WeakReference(value)
            } else {
                null
            }
        }
        get() {
            return targetImpl?.get()
        }

    override fun dispatchEvent(e: TemplateEvent<*>): Boolean {
        val result = target?.dispatchEvent(e) ?: false
        if (!result && e is ExecutableEvent<*>) {
            e.call()
            return true
        }
        return false
    }
}