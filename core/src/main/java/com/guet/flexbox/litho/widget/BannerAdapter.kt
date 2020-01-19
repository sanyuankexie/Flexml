package com.guet.flexbox.litho.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.Component

internal class BannerAdapter(
        val isCircular: Boolean,
        private val components: List<Component>
) : RecyclerView.Adapter<LithoViewHolder>() {

    fun getNormalizedPosition(position: Int): Int {
        return if (isCircular)
            position % realCount
        else
            position
    }

    val realCount: Int
        get() = components.size

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): LithoViewHolder {
        return LithoViewHolder(parent.context)
    }

    override fun getItemCount(): Int {
        return if (isCircular) {
            Int.MAX_VALUE
        } else {
            components.size
        }
    }

    override fun onViewRecycled(holder: LithoViewHolder) {
        holder.lithoView.unmountAllItems()
        holder.lithoView.componentTree = null
    }

    override fun onBindViewHolder(holder: LithoViewHolder, position: Int) {
        val p = getNormalizedPosition(position)
        holder.lithoView.setComponentAsync(components[p])
    }
}