package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.Orientation
import com.guet.flexbox.litho.LayoutThreadHandler
import com.guet.flexbox.litho.toPx
import java.lang.ref.WeakReference


@MountSpec(isPureRender = true, hasChildLithoViews = true)
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    @PropDefault
    val indicatorsHeightPx: Int = 5.toPx()

    @PropDefault
    val orientation = Orientation.HORIZONTAL

    @PropDefault
    val indicatorSelectedColor: Int = Color.WHITE

    @PropDefault
    val indicatorUnselectedColor: Int = Color.GRAY

    @OnCreateMountContent
    fun onCreateMountContent(c: Context): BannerView {
        return BannerView(c)
    }

    @OnMount
    fun mount(
            c: ComponentContext,
            view: BannerView,
            @Prop(optional = true) orientation: Orientation,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true) indicatorsHeightPx: Int,
            @Prop(optional = true) indicatorSelectedColor: Int,
            @Prop(optional = true) indicatorUnselectedColor: Int,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        if (!children.isNullOrEmpty()) {
            view.viewPager.adapter = BannerAdapter(
                    c,
                    isCircular,
                    createComponentTrees(
                            c,
                            children
                    )
            )
            view.viewPager.currentItem = children.size * 100
        }
        if (orientation == Orientation.HORIZONTAL) {
            view.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        } else {
            view.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        }
        view.indicatorsHeightPx = indicatorsHeightPx
        view.indicatorSelectedColor = indicatorSelectedColor
        view.indicatorUnselectedColor = indicatorUnselectedColor
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
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        val rect = Rect(0, 0, host.measuredWidth, host.measuredWidth)
        mutableListOf(host.indicators).apply {
            addAll((0 until host.viewPager.childCount).mapNotNull {
                host.viewPager.getChildAt(it) as? LithoView
            })
        }.forEach {
            it.performIncrementalMount(rect, false)
        }

        if (timeSpan > 0) {
            val token = CarouselRunnable(
                    host.viewPager,
                    timeSpan
            )
            host.token = token
            ConcurrentUtils.mainThreadHandler
                    .postDelayed(token, timeSpan)
        }
    }

    @OnUnbind
    fun onUnbind(
            c: ComponentContext,
            host: BannerView
    ) {
        host.token?.let {
            ConcurrentUtils.mainThreadHandler
                    .removeCallbacks(it)
        }
    }

}

class CarouselRunnable(
        host: ViewPager2,
        private val timeSpan: Long
) : WeakReference<ViewPager2>(host), Runnable {
    override fun run() {
        get()?.let {
            it.setCurrentItem(it.currentItem + 1, true)
            ConcurrentUtils.mainThreadHandler
                    .postDelayed(this, timeSpan)
        }
    }
}

private class LithoViewHolder(
        val c: ComponentContext,
        val lithoView: LithoView = LithoView(c).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
        }
) : RecyclerView.ViewHolder(lithoView)

private class BannerAdapter(
        private val c: ComponentContext,
        private val isCircular: Boolean,
        private val componentTrees: List<ComponentTree>
) : RecyclerView.Adapter<LithoViewHolder>() {

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
    ): LithoViewHolder {
        return LithoViewHolder(c)
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
    }

    override fun onBindViewHolder(holder: LithoViewHolder, position: Int) {
        val p = getNormalizedPosition(position)
        holder.lithoView.componentTree = componentTrees[p]
    }
}

class BannerView(context: Context) : FrameLayout(context), HasLithoViewChildren {

    val viewPager: ViewPager2 = ViewPager2(context)

    var token: CarouselRunnable? = null

    val indicators = LithoView(context)

    var indicatorsHeightPx: Int = BannerSpec.indicatorsHeightPx

    var indicatorSelectedColor: Int = BannerSpec.indicatorSelectedColor

    var indicatorUnselectedColor: Int = BannerSpec.indicatorUnselectedColor

    private val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val adapter = viewPager.adapter
            if (adapter !is BannerAdapter) {
                return
            }
            val realPosition = adapter.getNormalizedPosition(position)
            val indicatorPx = 5.toPx()
            val c = indicators.componentContext
            val outline = CornerOutlineProvider(indicatorPx)
            indicators.setComponentAsync(Row.create(c)
                    .justifyContent(YogaJustify.CENTER)
                    .alignItems(YogaAlign.FLEX_END)
                    .child(Row.create(c)
                            .marginPx(YogaEdge.BOTTOM, indicatorsHeightPx)
                            .apply {
                                (0 until adapter.realCount).forEach { index ->
                                    child(Row.create(c)
                                            .widthPx(indicatorPx)
                                            .heightPx(indicatorPx)
                                            .marginPx(YogaEdge.LEFT, indicatorPx / 2)
                                            .marginPx(YogaEdge.RIGHT, indicatorPx / 2)
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
