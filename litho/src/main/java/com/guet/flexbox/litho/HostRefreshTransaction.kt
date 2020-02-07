package com.guet.flexbox.litho

import android.view.View
import com.facebook.litho.Component
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.transaction.RefreshTransaction

internal class HostRefreshTransaction(
        private val host: HostingView,
        private val source: View?
) : RefreshTransaction() {
    override fun commit(): (ELContext) -> Unit {
        return create { elContext ->
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
                it.invoke(elContext)
            }
            val page = host.templatePage ?: return@create
            val c = host.componentContext
            AppExecutors.runOnAsyncThread {
                val component = LithoBuildTool.build(
                        page.template,
                        page.eventBridge,
                        elContext,
                        c
                ) as Component
                page.setRoot(component)
            }
        }
    }
}