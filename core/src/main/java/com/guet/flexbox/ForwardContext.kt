package com.guet.flexbox

import java.lang.ref.WeakReference

class ForwardContext : HostContext() {

    private var targetImpl: WeakReference<HostContext>? = null

    var target: HostContext?
        set(value) {
            targetImpl = if (value != null) {
                if (targetImpl?.get() != null) {
                    throw IllegalStateException(
                            "This Page is set to two HostingViews. " +
                                    "This is not allowed."
                    )
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