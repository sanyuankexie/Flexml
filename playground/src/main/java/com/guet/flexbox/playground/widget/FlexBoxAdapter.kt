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

    private inner class HandleClickWithUpdater(
            private val old: Page
    ) : PageEventAdapter() {

        override fun onEventDispatched(
                h: HostingView,
                source: View?,
                values: Array<out Any?>?
        ) {
            val url = values!![0] as? String
            if (url != null) {
                onClick(h, url)
            }
        }

        override fun onPageChanged(
                h: HostingView,
                page: Page
        ) {
            val index = data.indexOf(old)
            if (index != -1) {
                data[index] = page
                h.setPageEventListener(HandleClickWithUpdater(page))
            }
        }
    }

    override fun convert(helper: BaseViewHolder, item: Page) {
        val lithoView = helper.getView<HostingView>(R.id.litho)
        lithoView.setPageEventListener(HandleClickWithUpdater(item))
        lithoView.setContentAsync(item)
    }
}