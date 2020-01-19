package com.guet.flexbox.litho.widget

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.facebook.litho.LithoView

internal class LithoViewHolder(
        c: Context
) : ViewHolder(LithoView(c).apply {
    layoutParams = LayoutParams(-1, -1)
}) {
    val lithoView: LithoView
        get() = itemView as LithoView
}