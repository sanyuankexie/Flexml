package com.guet.flexbox

import android.util.Log
import java.lang.ref.WeakReference

class EventBridge : EventContext() {

    private var targetImpl: WeakReference<EventContext>? = null

    var target: EventContext?
        set(value) {
            targetImpl = if (value != null) {
                val t = target
                if (t != null && t != value) {
                    Log.e("EventBridge", "This Page is set to two HostingView. " +
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

    override fun dispatchEvent(key: ActionKey, args: List<Any?>?): Any? {
        return target?.dispatchEvent(key, args)
    }
}