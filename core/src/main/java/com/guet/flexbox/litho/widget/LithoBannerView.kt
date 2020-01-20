package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.*
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.Orientation
import com.guet.flexbox.litho.LayoutThreadHandler
import com.guet.flexbox.litho.toPx
import java.lang.ref.WeakReference

class LithoBannerView(context: Context) : FrameLayout(context),HasLithoViewChildren {
    private val adapter = InternalAdapter()
    private val viewPager2: ViewPager2 = ViewPager2(context)
    private val internalHost = viewPager2.getChildAt(0) as ViewGroup
    private val indicators = LithoView(context)

    private var indicatorHeightPx: Int = BannerSpec.indicatorHeightPx
    private var indicatorSelectedColor: Int = BannerSpec.indicatorSelectedColor
    private var indicatorUnselectedColor: Int = BannerSpec.indicatorUnselectedColor
    private var indicatorEnable: Boolean = BannerSpec.indicatorEnable
    private var isCircular: Boolean = BannerSpec.isCircular
    private val componentTrees = ArrayList<ComponentTree>()
    private var token: Runnable? = null

    val view: View
        get() = viewPager2

    init {
        addView(viewPager2, ViewGroup.LayoutParams(-1, -1))
        addView(indicators, ViewGroup.LayoutParams(-1, -1))
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                indicators.setComponentAsync(renderIndicators(position))
                (0 until internalHost.childCount).mapNotNull {
                    internalHost.getChildAt(it) as? LithoView
                }.forEach {
                    it.performIncrementalMount(rect, false)
                }
            }
        })
        viewPager2.adapter = adapter
    }

    private fun renderIndicators(position: Int): Component {
        val realPosition = getNormalizedPosition(position)
        val indicatorPx = 5.toPx()
        val outline = CornerOutlineProvider(indicatorPx)
        val c = indicators.componentContext
        val cc = (componentTrees.indices).map { index ->
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
            c: ComponentContext,
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
            children.forEach {
                componentTrees.add(ComponentTree.create(c)
                        .withRoot(it)
                        .isReconciliationEnabled(true)
                        .layoutThreadHandler(LayoutThreadHandler)
                        .build()
                )
            }
            viewPager2.setCurrentItem(if (isCircular) {
                children.size * 100
            } else {
                0
            }, false)
            adapter.notifyDataSetChanged()
        }

    }

    override fun obtainLithoViewChildren(lithoViews: MutableList<LithoView>) {
        (0 until internalHost.childCount).mapNotNull {
            internalHost.getChildAt(it) as? LithoView
        }.forEach {
            lithoViews.add(it)
        }
    }

    fun unmount() {
        componentTrees.clear()
        adapter.notifyDataSetChanged()
    }

    fun bind(timeSpan: Long) {
        indicators.performIncrementalMount(rect, false)
        (0 until internalHost.childCount).mapNotNull {
            internalHost.getChildAt(it) as? LithoView
        }.forEach {
            it.performIncrementalMount(rect, false)
        }
        if (timeSpan <= 0) {
            return
        }
        token = object : WeakReference<ViewPager2>(viewPager2), Runnable {
            override fun run() {
                get()?.let { viewPager ->
                    viewPager.currentItem = if (isCircular) {
                        viewPager2.currentItem + 1
                    } else {
                        getNormalizedPosition(
                                viewPager2.currentItem + 1
                        )
                    }
                    ConcurrentUtils.mainThreadHandler
                            .postDelayed(this, timeSpan)
                }
            }
        }
        ConcurrentUtils.mainThreadHandler
                .postDelayed(token, timeSpan)
    }

    fun unbind() {
        val token = token
        if (token != null) {
            ConcurrentUtils.mainThreadHandler
                    .removeCallbacks(token)
            this.token = null
        }
    }

    private fun getNormalizedPosition(position: Int): Int {
        if (componentTrees.isEmpty()) return 0
        return if (isCircular)
            position % componentTrees.size
        else
            position
    }

    companion object {

        private val rect = Rect(0, 0, Int.MAX_VALUE, Int.MAX_VALUE)
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
            return if (isCircular) {
                Int.MAX_VALUE
            } else {
                componentTrees.size
            }
        }

        override fun onViewRecycled(holder: LithoViewHolder) {

            holder.lithoView.unmountAllItems()
            holder.lithoView.componentTree = null
            holder.lithoView.resetMountStartupLoggingInfo()
        }

        override fun onBindViewHolder(holder: LithoViewHolder, position: Int) {
            holder.lithoView.componentTree = componentTrees[getNormalizedPosition(position)]
        }
    }

}