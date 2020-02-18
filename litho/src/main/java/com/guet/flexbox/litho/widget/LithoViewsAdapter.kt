package com.guet.flexbox.litho.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal abstract class LithoViewsAdapter : RecyclerView.Adapter<LithoViewHolder>() {
    final override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LithoViewHolder {
        return LithoViewHolder(parent.context)
    }

}