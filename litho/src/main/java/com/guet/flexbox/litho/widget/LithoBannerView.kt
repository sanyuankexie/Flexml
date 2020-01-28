package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Rect
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.Component
import com.facebook.litho.HasLithoViewChildren
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.toPx
import java.lang.ref.WeakReference

class LithoBannerView(context: Context) : FrameLayout(context), HasLithoViewChildren {
    private val adapter = InternalAdapter()
    private val viewPager2: ViewPager2 = ViewPager2(context)
    private val internalHost = viewPager2.getChildAt(0) as ViewGroup
    private val indicators = LithoView(context)

    private var indicatorHeightPx: Int = BannerSpec.indicatorHeightPx
    private var indicatorSelectedColor: Int = BannerSpec.indicatorSelectedColor
    private var indicatorUnselectedColor: Int = BannerSpec.indicatorUnselectedColor
    private var indicatorEnable: Boolean = BannerSpec.indicatorEnable
    private var isCircular: Boolean = BannerSpec.isCircular
    private val components = ArrayList<Component>()
    private var token: Runnable? = null

    init {
        addView(viewPager2, ViewGroup.LayoutParams(-1, -1))
        addView(indicators, ViewGroup.LayoutParams(-1, -1))
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicators.setComponentAsync(renderIndicators(position))
            }
        })
        viewPager2.adapter = adapter
    }

    private fun renderIndicators(position: Int): Component {
        val realPosition = getNormalizedPosition(position)
        val indicatorPx = 5.toPx()
        val outline = CornerOutlineProvider(indicatorPx)
        val c = indicators.componentContext
        val cc = (components.indices).map { index ->
            Row.create(c)
                    .widthPx(indicatorPx)
                    .heightPx(indicatorPx)
                    .marginPx(YogaEdge.LEFT, indicatorPx / 2)
                    .marginPx(YogaEdge.RIGHT, indicatorPx / 2)
                    .outlineProvider(outline)
                    .clipToOutline(true)
                    .apply {
                        if (index == realPosition) {
                            backgroundColor(indicatorSelectedColor)
                        } else {
                            backgroundColor(indicatorUnselectedColor)
                        }
                    }
        }
        return Row.create(c)
                .justifyContent(YogaJustify.CENTER)
                .alignItems(YogaAlign.FLEX_END)
                .child(Row.create(c)
                        .marginPx(YogaEdge.BOTTOM, indicatorHeightPx)
                        .apply {
                            cc.forEach {
                                child(it)
                            }
                        })
                .build()
    }

    fun mount(
            orientation: Orientation,
            isCircular: Boolean,
            indicatorHeightPx: Int,
            indicatorSelectedColor: Int,
            indicatorUnselectedColor: Int,
            indicatorEnable: Boolean,
            children: List<Component>?
    ) {
        viewPager2.orientation = when (orientation) {
            Orientation.HORIZONTAL -> ViewPager2.ORIENTATION_HORIZONTAL
            else -> ViewPager2.ORIENTATION_VERTICAL
        }
        this.isCircular = isCircular
        this.indicatorHeightPx = indicatorHeightPx
        this.indicatorSelectedColor = indicatorSelectedColor
        this.indicatorUnselectedColor = indicatorUnselectedColor
        this.indicatorEnable = indicatorEnable
        viewPager2.setCurrentItem(0, false)
        if (!children.isNullOrEmpty()) {
            this.components.addAll(children)
            viewPager2.setCurrentItem(if (isCircular) {
                children.size * 100
            } else {
                0
            }, false)
            adapter.notifyDataSetChanged()
        }
        indicators.setComponent(renderIndicators(0))
    }

    override fun obtainLithoViewChildren(lithoViews: MutableList<LithoView>) {
        (0 until internalHost.childCount).mapNotNull {
            internalHost.getChildAt(it) as? LithoView
        }.forEach {
            lithoViews.add(it)
        }
    }

    fun unmount() {
        components.clear()
        adapter.notifyDataSetChanged()
    }

    fun bind(timeSpan: Long) {
        indicators.performIncrementalMount(rect, false)
        indicators.setVisibilityHint(true)
        if (timeSpan <= 0) {
            return
        }
        val token = object : WeakReference<ViewPager2>(viewPager2), Runnable {
            override fun run() {
                get()?.let { viewPager ->
                    viewPager.currentItem = if (isCircular) {
                        viewPager2.currentItem + 1
                    } else {
                        getNormalizedPosition(
                                viewPager2.currentItem + 1
                        )
                    }
                    AppExecutors.mainThreadHandler
                            .postDelayed(this, timeSpan)
                }
            }
        }
        this.token = token
        AppExecutors.mainThreadHandler
                .postDelayed(token, timeSpan)
    }

    fun unbind() {
        indicators.setVisibilityHint(false)
        val token = token
        if (token != null) {
            AppExecutors.mainThreadHandler
                    .removeCallbacks(token)
            this.token = null
        }
    }

    private fun getNormalizedPosition(position: Int): Int {
        if (components.isEmpty()) return 0
        return if (isCircular)
            position % components.size
        else
            position
    }

    companion object {
        private val rect = Rect(
                Int.MIN_VALUE,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                Int.MAX_VALUE
        )
    }

    private class LithoViewHolder(
            c: Context
    ) : RecyclerView.ViewHolder(LithoView(c).apply {
        layoutParams = RecyclerView.LayoutParams(-1, -1)
    }) {
        val lithoView: LithoView
            get() = itemView as LithoView
    }

    private inner class InternalAdapter : RecyclerView.Adapter<LithoViewHolder>() {

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): LithoViewHolder {
            return LithoViewHolder(parent.context)
        }

        override fun getItemCount(): Int {
            if (components.isEmpty())
                return 0
            return if (isCircular) {
                Int.MAX_VALUE
            } else {
                components.size
            }
        }

        override fun onViewRecycled(holder: LithoViewHolder) {
            holder.lithoView.unmountAllItems()
            holder.lithoView.setVisibilityHint(false)
        }


        override fun onBindViewHolder(holder: LithoViewHolder, position: Int) {
            val pos = getNormalizedPosition(position)
            holder.lithoView.setComponent(components[pos])
            holder.lithoView.setVisibilityHint(true)
        }
    }

}