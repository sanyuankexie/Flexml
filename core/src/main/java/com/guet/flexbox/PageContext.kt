package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

class PageContext(
        private val view: View?,
        private val host: HostContext
) {

    fun send(vararg values: Any?) {
        host.dispatchEvent(
                HostContext.ActionKey.SendObjects,
                mutableListOf<Any?>(view).apply {
                    addAll(values)
                }
        )
    }

    fun http(): HttpTransaction? {
        return host.dispatchEvent(
                HostContext.ActionKey.HttpRequest,
                listOf(view)
        ) as? HttpTransaction
    }

    fun refresh(): RefreshTransaction? {
        return host.dispatchEvent(
                HostContext.ActionKey.RefreshPage,
                listOf(view)
        ) as? RefreshTransaction
    }
}
