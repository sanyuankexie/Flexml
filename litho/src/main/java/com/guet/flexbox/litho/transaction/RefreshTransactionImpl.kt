package com.guet.flexbox.litho.transaction

import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.SizeSpec
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.ForwardContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.LithoBuildUtils
import com.guet.flexbox.litho.Page
import com.guet.flexbox.transaction.RefreshTransaction

internal class RefreshTransactionImpl(
        private val host: HostingView,
        private val source: View
) : RefreshTransaction() {
    override fun commit(): (ELContext) -> Unit {
        return { elContext ->
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
            val node = host.template
            val tree = host.componentTree
            if (tree != null && node != null) {
                val c = host.componentContext
                val height = host.layoutParams?.width ?: 0
                val mH = host.measuredHeight
                val mW = host.measuredWidth
                ConcurrentUtils.runOnAsyncThread {
                    val context = ForwardContext()
                            .apply {
                                target = host.pageContext
                            }
                    val component = LithoBuildUtils.build(
                            node,
                            context,
                            elContext,
                            true,
                            c
                    ).single() as Component
                    tree.setRootAndSizeSpec(
                            component,
                            SizeSpec.makeSizeSpec(mW, SizeSpec.EXACTLY),
                            when (height) {
                                ViewGroup.LayoutParams.WRAP_CONTENT ->
                                    SizeSpec.makeSizeSpec(
                                            0,
                                            SizeSpec.UNSPECIFIED
                                    )
                                else ->
                                    SizeSpec.makeSizeSpec(
                                            mH,
                                            SizeSpec.EXACTLY
                                    )
                            })
                    ConcurrentUtils.runOnUiThread {
                        host.pageEventListener?.onPageChanged(
                                host,
                                Page(node, component, context)
                        )
                    }
                }
            }
        }
    }
}