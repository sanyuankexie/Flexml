package com.guet.flexbox.playground.widget

import android.content.Context
import android.view.View
import com.guet.flexbox.PageHostView
import com.guet.flexbox.content.RenderContent
import com.zhouwei.mzbanner.holder.MZHolderCreator
import com.zhouwei.mzbanner.holder.MZViewHolder

class BannerAdapter : MZHolderCreator<BannerAdapter.BannerHolder> {

    override fun createViewHolder(): BannerHolder {
        return BannerHolder()
    }

    inner class BannerHolder : MZViewHolder<RenderContent> {

        private lateinit var lithoView: PageHostView

        override fun onBind(p0: Context?, p1: Int, item: RenderContent) {
            val c = lithoView.componentContext
            lithoView.unmountAllItems()
            lithoView.setContentAsync(item)
        }

        override fun createView(c: Context): View {
            lithoView = PageHostView(c)
            return lithoView
        }
    }
}


