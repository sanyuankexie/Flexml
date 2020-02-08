package com.guet.flexbox.litho.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.widget.ComponentRenderInfo
import com.facebook.litho.widget.ComponentTreeHolder
import com.guet.flexbox.litho.LayoutThreadHandler

class BannerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val componentTreeHolders = ArrayList<ComponentTreeHolder>()

    private val viewPager2 = ViewPager2(context)

    private var indicatorHeightPx: Int = BannerSpec.indicatorHeightPx
    private var indicatorSelectedColor: Int = BannerSpec.indicatorSelectedColor
    private var indicatorUnselectedColor: Int = BannerSpec.indicatorUnselectedColor
    private var indicatorEnable: Boolean = BannerSpec.indicatorEnable
    private var isCircular: Boolean = BannerSpec.isCircular
    private var token: Runnable? = null

    init {
        addView(viewPager2)
    }

    fun unmount() {

    }

    fun mount(c: ComponentContext, toAsync: List<Component>) {
        if (toAsync.isNotEmpty()) {
            val holders = toAsync.map {
                ComponentTreeHolder.create()
                        .renderInfo(ComponentRenderInfo.create()
                                .component(it)
                                .build())
                        .layoutHandler(LayoutThreadHandler)
                        .isReconciliationEnabled(false)
                        .build()
            }
        }
    }

    private class BannerItemHolder(
            c: Context
    ) : RecyclerView.ViewHolder(LithoView(c).apply {
        layoutParams = RecyclerView.LayoutParams(-1, -1)
    }) {
        val bannerItem: LithoView
            get() = super.itemView as LithoView
    }

    private fun getNormalizedPosition(position: Int): Int {
        if (componentTreeHolders.isEmpty()) return 0
        return if (isCircular)
            position % componentTreeHolders.size
        else
            position
    }

    private class BannerItemAdapter : RecyclerView.Adapter<BannerItemHolder>() {

        override fun onBindViewHolder(
                holder: BannerItemHolder,
                position: Int
        ) {

        }

        override fun onViewRecycled(holder: BannerItemHolder) {

        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): BannerItemHolder {
            TODO()
        }

        override fun getItemCount(): Int {
            TODO()
        }
    }
}