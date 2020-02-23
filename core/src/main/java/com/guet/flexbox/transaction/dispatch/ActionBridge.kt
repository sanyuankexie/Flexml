package com.guet.flexbox.transaction.dispatch

import android.util.Log
import android.view.View
import com.guet.flexbox.transaction.impl.PageContextImpl
import java.lang.ref.WeakReference

class ActionBridge : ActionTarget {

    private var targetImpl: WeakReference<ActionTarget>? = null

    var target: ActionTarget?
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

    override fun dispatchAction(
            key: ActionKey,
            source: View?,
            args: Array<out Any?>?
    ) {
        target?.dispatchAction(key, source, args)
    }

    internal fun newPageContext(): PageContextImpl {
        return PageContextImpl(this)
    }
}