package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

class PageContext(
        view: View?,
        private val host: HostContext
) {

    private val source = arrayOf(view)

    fun send(vararg values: Any?) {
        host.dispatchEvent(
                HostContext.ActionKey.SendObjects,
                source.zip(values)
        )
    }

    fun http(): HttpTransaction? {
        return host.dispatchEvent(
                HostContext.ActionKey.HttpRequest,
                source
        ) as? HttpTransaction
    }

    fun refresh(): RefreshTransaction? {
        return host.dispatchEvent(
                HostContext.ActionKey.RefreshPage,
                source
        ) as? RefreshTransaction
    }
}
