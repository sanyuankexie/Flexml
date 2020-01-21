package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.HostingContext
import com.guet.flexbox.litho.transaction.HttpTransactionImpl
import com.guet.flexbox.litho.transaction.RefreshTransactionImpl
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

internal class HostingContextImpl(
        private val host: HostingView
) : HostingContext() {

    override fun send(source: View, values: Array<out Any?>) {
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