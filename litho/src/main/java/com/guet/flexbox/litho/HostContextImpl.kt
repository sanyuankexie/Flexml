package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.HostContext
import com.guet.flexbox.HostContext.ActionKey.*

internal class HostContextImpl(
        private val host: HostingView
) : HostContext() {

    override fun dispatchEvent(key: ActionKey, args: List<Any?>?): Any? {
        return when (key) {
            SendObjects -> {
                host.pageEventListener?.onEventDispatched(
                        host,
                        args?.get(0) as? View,
                        args?.let { it.subList(1,it.size).toTypedArray() }
                )
            }
            RefreshPage -> RefreshTransactionImpl(
                    host,
                    args?.get(0) as? View
            )
            HttpRequest -> HttpTransactionImpl(
                    host,
                    args?.get(0) as? View
            )
        }
    }
}