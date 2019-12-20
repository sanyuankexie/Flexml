package com.guet.flexbox.playground.widget

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.playground.R


class FlexBoxAdapter : BaseQuickAdapter<RenderNode, BaseViewHolder>(R.layout.feed_item) {

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<LithoView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: RenderNode) {
        val lithoView = helper.getView<LithoView>(R.id.litho)
        val c = lithoView.componentContext
        lithoView.setComponentAsync(Row.create(c)
                .alignItems(YogaAlign.CENTER)
                .flexGrow(1f)
                .justifyContent(YogaJustify.CENTER)
                .child(DynamicBox.create(c)
                        .content(item)
                ).build())
    }
}