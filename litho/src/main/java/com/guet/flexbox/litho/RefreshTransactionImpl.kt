package com.guet.flexbox.litho

import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.SizeSpec
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.transaction.RefreshTransaction

internal class RefreshTransactionImpl(
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
            val page = host.page ?: return@create
            val c = host.componentContext
            ConcurrentUtils.runOnAsyncThread {
                val component = LithoBuildTool.build(
                        page.template,
                        page.forward,
                        elContext,
                        c
                ) as Component
                ConcurrentUtils.runOnUiThread {
                    page.display = component
                    val tree = host.componentTree
                            ?: return@runOnUiThread
                    tree.setRootAndSizeSpecAsync(
                            component,
                            SizeSpec.makeSizeSpec(
                                    host.measuredWidth,
                                    SizeSpec.EXACTLY
                            ),
                            when (host.layoutParams?.width ?: 0) {
                                ViewGroup.LayoutParams.WRAP_CONTENT ->
                                    SizeSpec.makeSizeSpec(
                                            0,
                                            SizeSpec.UNSPECIFIED
                                    )
                                else ->
                                    SizeSpec.makeSizeSpec(
                                            host.measuredHeight,
                                            SizeSpec.EXACTLY
                                    )
                            })
                }
            }
        }
    }
}