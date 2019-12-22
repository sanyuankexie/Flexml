package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.litho.PageHostView
import com.guet.flexbox.playground.R
import com.guet.flexbox.widget.EventHandler


class FlexBoxAdapter(
        private val onClickListener: (v: View, url: String) -> Unit
) : BaseQuickAdapter<RenderContent, BaseViewHolder>(R.layout.feed_item), EventHandler {

    override fun handleEvent(v: View, key: String, value: Any) {
        onClickListener(v, key)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<PageHostView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: RenderContent) {
        val lithoView = helper.getView<PageHostView>(R.id.litho)
        lithoView.eventHandler = this
        lithoView.setContentAsync(item,"Feed")
    }
}