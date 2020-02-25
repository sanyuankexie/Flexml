package com.guet.flexbox.playground.widget

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.guet.flexbox.eventsystem.EventHandler
import com.guet.flexbox.eventsystem.event.ClickUrlEvent
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.TemplatePage
import com.guet.flexbox.playground.R


class FlexBoxAdapter(
        private val onClick: (v: View, url: String) -> Unit
) : BaseQuickAdapter<TemplatePage, BaseViewHolder>(R.layout.feed_item) {

    private val callback = object : EventHandler<ClickUrlEvent> {
        override fun handleEvent(e: ClickUrlEvent): Boolean {
            onClick(e.source, e.url)
            return true
        }
    }


    override fun onViewRecycled(holder: BaseViewHolder) {
        val lithoView = holder.getView<HostingView>(R.id.litho)
        lithoView?.unmountAllItems()
        lithoView?.templatePage = null
    }

    override fun convert(helper: BaseViewHolder, item: TemplatePage) {
        val lithoView = helper.getView<HostingView>(R.id.litho)
        lithoView.eventBus.subscribe(callback)
        lithoView.templatePage = item
    }
}