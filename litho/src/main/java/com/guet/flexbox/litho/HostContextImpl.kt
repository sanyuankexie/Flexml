package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.HostContext
import com.guet.flexbox.litho.transaction.HttpTransactionImpl
import com.guet.flexbox.litho.transaction.RefreshTransactionImpl
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

internal class HostContextImpl(
        private val host: HostingView
) : HostContext() {

    override fun send(source: View?, values: Array<out Any?>?) {
        host.pageEventListener?.onEventDispatched(
                host,
                source,
                values
        )
    }

    override fun http(source: View): HttpTransaction? {
        return HttpTransactionImpl(host, source)
    }

    override fun refresh(source: View): RefreshTransaction? {
        return RefreshTransactionImpl(host, source)
    }

}