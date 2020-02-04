package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.EventContext
import com.guet.flexbox.EventContext.ActionKey.*

internal class EventTarget(
        private val host: HostingView
) : EventContext() {
    override fun dispatchEvent(key: ActionKey, args: List<Any?>?): Any? {
        return when (key) {
            SendObjects -> {
                host.pageEventListener?.onEventDispatched(
                        host,
                        args?.get(0) as? View,
                        args?.let { it.subList(1,it.size).toTypedArray() }
                )
            }
            RefreshPage -> HostRefreshTransaction(
                    host,
                    args?.get(0) as? View
            )
            HttpRequest -> HostHttpTransaction(
                    host,
                    args?.get(0) as? View
            )
        }
    }
}