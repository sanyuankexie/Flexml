package com.guet.flexbox.litho.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.guet.flexbox.litho.LayoutThreadHandler

internal class LithoViewHolder(
        c: ComponentContext,
        val lithoView: LithoView = LithoView(c).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            componentTree = ComponentTree.create(c)
                    .layoutThreadHandler(LayoutThreadHandler)
                    .isReconciliationEnabled(false)
                    .build()
        }
) : RecyclerView.ViewHolder(lithoView)