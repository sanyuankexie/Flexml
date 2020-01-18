package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.litho.LayoutThreadHandler
import com.guet.flexbox.litho.toPx


@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): BannerView {
        return BannerView(c)
    }

    @OnMount
    fun mount(
            c: ComponentContext,
            view: BannerView,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        if (!children.isNullOrEmpty()) {
            view.viewPager.adapter = InternalAdapter(
                    c,
                    isCircular,
                    createComponentTrees(
                            c,
                            children
                    )
            )
            view.viewPager.currentItem = children.size * 100
        }
    }

    @OnUnmount
    fun unmount(
            c: ComponentContext,
            view: BannerView
    ) {
        view.viewPager.adapter = null
    }

    private fun createComponentTrees(
            c: ComponentContext,
            children: List<Component>
    ): List<ComponentTree> {
        return children.map {
            ComponentTree.create(c)
                    .withRoot(it)
                    .isReconciliationEnabled(false)
                    .layoutThreadHandler(LayoutThreadHandler)
                    .build()
        }
    }

    @OnBind
    fun onBind(
            c: ComponentContext,
            host: BannerView,
            @Prop(optional = true) timeSpan: Long
    ) {
        val rect = Rect(0, 0, host.measuredWidth, host.measuredWidth)
        mutableListOf(host.indicators).apply {
            addAll((0 until host.viewPager.childCount).mapNotNull {
                host.viewPager.getChildAt(it) as? LithoView
            })
        }.forEach {
            it.performIncrementalMount(rect, false)
        }
        if (timeSpan <= 100) {
            return
        }
        HandlerCompat.postDelayed(
                ConcurrentUtils.mainThreadHandler,
                CarouselRunnable(host.viewPager, timeSpan),
                host.viewPager,
                timeSpan
        )

    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            host: BannerView
    ) {
        ConcurrentUtils.mainThreadHandler
                .removeCallbacksAndMessages(host.viewPager)
    }

}

private class CarouselRunnable(
        private val host: ViewPager2,
        private val timeSpan: Long
) : Runnable {

    override fun run() {
        host.setCurrentItem(host.currentItem + 1, true)
        HandlerCompat.postDelayed(
                ConcurrentUtils.mainThreadHandler,
                this,
                host,
                timeSpan
        )
    }

}

private class InternalViewHolder(
        val c: ComponentContext,
        val lithoView: LithoView = LithoView(c).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
) : RecyclerView.ViewHolder(lithoView)

private class InternalAdapter(
        private val c: ComponentContext,
        private val isCircular: Boolean,
        private val componentTrees: List<ComponentTree>
) : RecyclerView.Adapter<InternalViewHolder>() {

    fun getNormalizedPosition(position: Int): Int {
        return if (isCircular)
            position % componentTrees.size
        else
            position
    }

    val realCount: Int
        get() = componentTrees.size

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): InternalViewHolder {
        return InternalViewHolder(c)
    }

    override fun getItemCount(): Int {
        return if (isCircular) {
            Int.MAX_VALUE
        } else {
            componentTrees.size
        }
    }

    override fun onViewRecycled(holder: InternalViewHolder) {
        holder.lithoView.unmountAllItems()
        holder.lithoView.componentTree = null
    }

    override fun onBindViewHolder(holder: InternalViewHolder, position: Int) {
        val p = getNormalizedPosition(position)
        holder.lithoView.componentTree = componentTrees[p]
    }
}

class BannerView(context: Context) : FrameLayout(context), HasLithoViewChildren {

    val viewPager: ViewPager2 = ViewPager2(context)

    val indicators = LithoView(context)

    private val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val adapter = viewPager.adapter
            if (adapter !is InternalAdapter) {
                return
            }
            val realPosition = adapter.getNormalizedPosition(position)
            val c = indicators.componentContext
            val r = 1.5
            val px = r.toPx()
            val px2 = (r * 4).toPx()
            val outline = CornerOutlineProvider(px2)
            indicators.setComponentAsync(Row.create(c)
                    .justifyContent(YogaJustify.CENTER)
                    .alignItems(YogaAlign.FLEX_END)
                    .child(Row.create(c)
                            .marginPx(YogaEdge.BOTTOM, px2)
                            .apply {
                                (0 until adapter.realCount).forEach { index ->
                                    child(Row.create(c)
                                            .widthPx(px2)
                                            .heightPx(px2)
                                            .marginPx(YogaEdge.LEFT, px)
                                            .marginPx(YogaEdge.RIGHT, px)
                                            .outlineProvider(outline)
                                            .clipToOutline(true)
                                            .apply {
                                                if (index == realPosition) {
                                                    backgroundColor(Color.WHITE)
                                                } else {
                                                    backgroundColor(Color.GRAY)
                                                }
                                            }
                                    )
                                }
                            })
                    .build())
        }
    }

    init {
        addView(viewPager, LayoutParams(-1, -1))
        addView(indicators, LayoutParams(-1, -1))
        viewPager.registerOnPageChangeCallback(callback)
    }

    override fun obtainLithoViewChildren(lithoViews: MutableList<LithoView>) {
        lithoViews.add(indicators)
        for (i in 0..viewPager.childCount) {
            val child: View = viewPager.getChildAt(i)
            if (child is LithoView) {
                lithoViews.add(child)
            }
        }
    }

}
