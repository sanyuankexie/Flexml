package com.guet.flexbox.playground.widget

import android.content.Context
import android.view.View
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.EventListener
import com.guet.flexbox.data.RenderNode
import com.zhouwei.mzbanner.holder.MZViewHolder

class BannerHolder(private val onClickListener: ((String) -> Unit))
    : MZViewHolder<RenderNode>, EventListener {

    private lateinit var lithoView: LithoView

    override fun onBind(p0: Context?, p1: Int, item: RenderNode) {
        val c = lithoView.componentContext
        lithoView.release()
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

    override fun handleEvent(key: String, value: Array<out Any>) {
        onClickListener.invoke(key)
    }
}