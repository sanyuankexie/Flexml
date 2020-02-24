package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClick: (v: View, url: String) -> Unit
) : BaseQuickAdapter<TemplatePage, BaseViewHolder>(R.layout.feed_item) {

    init {
        closeLoadAnimation()
    }

    private val callback = object : HostingView.PageEventListener {
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
    }


    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<HostingView>(R.id.litho)
        lithoView?.unmountAllItems()
        lithoView?.templatePage = null
    }

    override fun convert(helper: BaseViewHolder, item: TemplatePage) {
        val lithoView = helper.getView<HostingView>(R.id.litho)
        lithoView.pageEventListener = callback
        lithoView.templatePage = item
    }
}