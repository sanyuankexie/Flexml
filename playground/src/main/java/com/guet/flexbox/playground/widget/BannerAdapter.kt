package com.guet.flexbox.playground.widget

import android.content.Context
import android.view.View
import com.guet.flexbox.litho.HostingView
import com.guet.flexbox.litho.Page
import com.zhouwei.mzbanner.holder.MZHolderCreator
import com.zhouwei.mzbanner.holder.MZViewHolder

class BannerAdapter : MZHolderCreator<BannerAdapter.BannerHolder> {

    override fun createViewHolder(): BannerHolder {
        return BannerHolder()
    }

    inner class BannerHolder : MZViewHolder<Page> {

        private lateinit var lithoView: HostingView

        override fun onBind(p0: Context?, p1: Int, item: Page) {
            val c = lithoView.componentContext
            lithoView.unmountAllItems()
            lithoView.setContentAsync(item)
        }

        override fun createView(c: Context): View {
            lithoView = HostingView(c)
            return lithoView
        }
    }
}


