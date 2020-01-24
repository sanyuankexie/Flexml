package com.guet.flexbox

import java.lang.ref.WeakReference

class ForwardContext : HostContext() {

    private var targetImpl: WeakReference<HostContext>? = null

    var target: HostContext?
        set(value) {
            targetImpl = if (value != null) {
                WeakReference(value)
            } else {
                null
            }
        }
        get() {
            return targetImpl?.get()
        }

    override fun dispatchEvent(key: ActionKey, vararg args: Any?): Any? {
        return target?.dispatchEvent(key, *args)
    }
}