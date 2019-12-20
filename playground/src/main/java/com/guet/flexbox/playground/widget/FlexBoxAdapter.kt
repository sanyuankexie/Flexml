package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.EventHandler
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.playground.R


class FlexBoxAdapter(private val onClickListener: (v: View, url: String) -> Unit) : BaseQuickAdapter<RenderContent, BaseViewHolder>(R.layout.feed_item) {

    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<LithoView>(R.id.litho)
        lithoView?.unmountAllItems()
    }

    override fun convert(helper: BaseViewHolder, item: RenderContent) {
        val lithoView = helper.getView<LithoView>(R.id.litho)
        val c = lithoView.componentContext
        item.setEventListener(object : EventHandler {
            override fun handleEvent(key: String, value: Array<out Any>) {
                onClickListener(lithoView, key)
            }
        })
        lithoView.setComponentAsync(Row.create(c)
                .alignItems(YogaAlign.CENTER)
                .flexGrow(1f)
                .justifyContent(YogaJustify.CENTER)
                .child(DynamicBox.create(c)
                        .content(item)
                ).build())
    }
}