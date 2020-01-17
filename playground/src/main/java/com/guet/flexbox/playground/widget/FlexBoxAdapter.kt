package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.Page
import com.guet.flexbox.litho.PageEventAdapter
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClick: (v: View, url: String) -> Unit
) : BaseQuickAdapter<Page, BaseViewHolder>(R.layout.feed_item) {


    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<HostingView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: Page) {
        val lithoView = helper.getView<HostingView>(R.id.litho)
        lithoView.setPageEventListener(object : PageEventAdapter() {
            override fun onEventDispatched(
                    h: HostingView,
                    source: View,
                    vararg values: Any?
            ) {
                val url = values[0] as? String
                if (url != null) {
                    onClick(source, url)
                }
            }

            override fun onPageChanged(page: Page, data: Any?) {
                getData()[getData().indexOf(item)] = page
            }
        })
        lithoView.setContentAsync(item)
    }
}