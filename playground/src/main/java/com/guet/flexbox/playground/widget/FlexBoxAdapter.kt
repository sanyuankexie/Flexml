package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.litho.LithoView
import com.guet.flexbox.PageHostView
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClickListener: (v: View, url: String) -> Unit
) : BaseQuickAdapter<RenderContent, BaseViewHolder>(R.layout.feed_item), PageHostView.EventListener {

    override fun onEvent(v: PageHostView, key: String, value: Any) {
        onClickListener(v, key)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<LithoView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: RenderContent) {
        val lithoView = helper.getView<PageHostView>(R.id.litho)
        lithoView.eventListener = this
        lithoView.setContentAsync(item)
    }
}