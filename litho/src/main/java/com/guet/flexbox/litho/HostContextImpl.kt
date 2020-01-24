package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.HostContext
import com.guet.flexbox.HostContext.ActionKey.*

internal class HostContextImpl(
        private val host: HostingView
) : HostContext() {

    override fun dispatchEvent(key: ActionKey, vararg args: Any?): Any? {
        return when (key) {
            SendObjects -> {
                host.pageEventListener?.onEventDispatched(
                        host,
                        args[0] as? View,
                        args.copyOfRange(1, args.size)
                )
            }
            RefreshPage -> HttpTransactionImpl(
                    host,
                    args[0] as? View
            )
            HttpRequest -> RefreshTransactionImpl(
                    host,
                    args[0] as? View
            )
        }
    }
}