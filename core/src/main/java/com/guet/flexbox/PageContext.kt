package com.guet.flexbox

import android.view.View
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

class PageContext(
        private val source: View,
        private val host: HostingContext
) {

    fun send(vararg values: Any?) {
        host.send(source, values)
    }

    fun http(): HttpTransaction? {
        return host.http(source)
    }

    fun refresh(): RefreshTransaction? {
        return host.refresh(source)
    }
}
