package com.guet.flexbox.litho

import android.view.View
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.transaction.RefreshTransaction

internal class HostRefreshTransaction(
        private val host: HostingView,
        private val source: View?
) : RefreshTransaction() {
    override fun commit(){
        host.pageEventListener?.run {
            sends.forEach {
                onEventDispatched(
                        host,
                        source,
                        it
                )
            }
        }
        actions.forEach {
            it.invoke()
        }
        val page = host.templatePage ?: return
        AppExecutors.runOnAsyncThread {
            page.computeNewLayout()
        }
    }
}