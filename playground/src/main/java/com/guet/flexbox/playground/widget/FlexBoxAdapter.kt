package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.PageHostView
import com.guet.flexbox.PreloadPage
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClick: (v: View, url: String) -> Unit
) : BaseQuickAdapter<PreloadPage, BaseViewHolder>(R.layout.feed_item), PageHostView.EventHandler {

    override fun handleEvent(v: View, key: String, value: Array<out Any>) {
        onClick(v, key)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<PageHostView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: PreloadPage) {
        val lithoView = helper.getView<PageHostView>(R.id.litho)
        lithoView.setEventHandler(this)
        lithoView.setContentAsync(item)
    }
}