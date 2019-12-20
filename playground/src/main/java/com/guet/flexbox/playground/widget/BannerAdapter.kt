package com.guet.flexbox.playground.widget

import android.content.Context
import android.view.View
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.content.RenderContent
import com.zhouwei.mzbanner.holder.MZHolderCreator
import com.zhouwei.mzbanner.holder.MZViewHolder

class BannerAdapter: MZHolderCreator<BannerAdapter.BannerHolder> {

    override fun createViewHolder(): BannerHolder {
        return BannerHolder()
    }

    inner class BannerHolder : MZViewHolder<RenderContent> {

        private lateinit var lithoView: LithoView

        override fun onBind(p0: Context?, p1: Int, item: RenderContent) {
            val c = lithoView.componentContext
            lithoView.unmountAllItems()
            lithoView.setComponentAsync(Row.create(c)
                    .flexGrow(1f)
                    .child(DynamicBox.create(c)
                            .flexGrow(1f)
                            .content(item)
                    ).build())
        }

        override fun createView(c: Context?): View {
            lithoView = LithoView(c)
            return lithoView
        }
    }
}


