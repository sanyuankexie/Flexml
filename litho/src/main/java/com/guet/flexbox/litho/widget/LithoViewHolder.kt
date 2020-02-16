package com.guet.flexbox.litho.widget

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.LithoView

internal class LithoViewHolder(
        c: Context
) : RecyclerView.ViewHolder(LithoView(c).apply {
    layoutParams = RecyclerView.LayoutParams(-1, -1)
}) {
    val lithoView: LithoView
        get() = itemView as LithoView
}