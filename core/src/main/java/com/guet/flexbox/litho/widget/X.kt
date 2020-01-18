package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.litho.LayoutThreadHandler
import com.guet.flexbox.litho.toPx
import java.util.*
import kotlin.collections.ArrayList


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
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ) {
        if (!children.isNullOrEmpty()) {
            view.viewPager.adapter = InternalAdapter(
                    c,
                    isCircular,
                    createComponentTrees(
                            c,
                            isCircular,
                            children
                    )
            )
            mount(view.viewPager, timeSpan)
        }
    }

    @OnUnmount
    fun unmount(
            c: ComponentContext,
            view: BannerView
    ) {
        view.viewPager.adapter = null
        unmount(view.viewPager)
    }

    private fun createComponentTrees(
            c: ComponentContext,
            isCircular: Boolean,
            children: List<Component>
    ): List<ComponentTree> {
        val list = if (isCircular) {
            ensureCircularCount(children)
        } else {
            children
        }
        return list.map {
            ComponentTree.create(c)
                    .withRoot(it)
                    .isReconciliationEnabled(false)
                    .layoutThreadHandler(LayoutThreadHandler)
                    .build()
        }
    }

    private fun ensureCircularCount(
            children: List<Component>
    ): List<Component> {
        return if (children.size < 4 && isCircular) {
            val newList = ArrayList<Component>(4)
            do {
                newList.addAll(children)
            } while (newList.size < 4)
            newList
        } else {
            children
        }
    }

    private fun mount(host: ViewPager, timeSpan: Long) {
//        HandlerCompat.postDelayed(
//                UiThread,
//                CarouselRunnable(host, timeSpan),
//                host,
//                timeSpan)
    }

    private fun unmount(host: ViewPager) {
//        UiThread.removeCallbacksAndMessages(host)
    }

}

private class CarouselRunnable(
        private val host: ViewPager,
        private val timeSpan: Long
) : Runnable {

    override fun run() {
        host.setCurrentItem(host.currentItem + 1, true)
//        HandlerCompat.postDelayed(
//                UiThread,
//                this,
//                host,
//                timeSpan)
    }

}

private class InternalAdapter(
        private val c: ComponentContext,
        val isCircular: Boolean,
        private val componentTrees: List<ComponentTree>
) : PagerAdapter() {

    override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
    ) {
        val lithoView = `object` as LithoView
        lithoView.unmountAllItems()
        lithoView.componentTree = null
        lithoView.setInvalidStateLogParamsList(null)
        lithoView.resetMountStartupLoggingInfo()
        container.removeView(lithoView)
        container.recycleLithoView(lithoView)
    }

    private fun getNormalizedPosition(position: Int): Int {
        return if (isCircular)
            position % componentTrees.size
        else
            position
    }

    val realCount: Int
        get() = componentTrees.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return if (isCircular) {
            Int.MAX_VALUE
        } else {
            componentTrees.size
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = container.obtainLithoView(c)
        container.addView(v)
        v.componentTree = componentTrees[getNormalizedPosition(position)]
        return v
    }

    companion object {

        private fun View.recycleLithoView(
                v: LithoView) {
            val cache = this.cache
            cache.push(v)
        }

        private fun View.obtainLithoView(
                c: ComponentContext
        ): LithoView {
            val cache = this.cache
            return if (cache.isEmpty()) {
                LithoView(c)
            } else {
                cache.pop()
            }
        }

        private val View.cache: LinkedList<LithoView>
            get() {
                @Suppress("UNCHECKED_CAST")
                var cache = this.tag as? LinkedList<LithoView>
                if (cache == null) {
                    cache = LinkedList()
                    this.tag = cache
                }
                return cache
            }
    }
}

class BannerView(context: Context) : FrameLayout(context), HasLithoViewChildren {

    val viewPager: ViewPager = InfiniteViewPager(context)

    private val indicators = LithoView(context)

    init {
        addView(viewPager, LayoutParams(-1, -1))
        addView(indicators, LayoutParams(-1, -1))
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val adapter = viewPager.adapter
                if ((adapter?.count ?: 0) == 0 || adapter !is InternalAdapter) {
                    return
                }
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
                                                    if (index == viewPager.currentItem) {
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
        })
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

private class InfiniteViewPager(context: Context) : ViewPager(context) {

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        // offset first element so that we can scroll to the left
        currentItem = 0
    }

    override fun setCurrentItem(item: Int) {
        // offset the current item to ensure there is space to scroll
        setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val adapter = adapter
        if ((adapter?.count ?: 0) == 0) {
            super.setCurrentItem(item, smoothScroll)
            return
        }
        val item1 = offsetAmount + item % (adapter?.count ?: 0)
        super.setCurrentItem(item1, smoothScroll)
    }

    override fun getCurrentItem(): Int {
        val adapter = adapter
        if ((adapter?.count ?: 0) == 0) {
            return super.getCurrentItem()
        }
        val position = super.getCurrentItem()
        return if (adapter is InternalAdapter && adapter.isCircular) {
            // Return the actual item position in the data backing InfinitePagerAdapter
            position % adapter.realCount
        } else {
            super.getCurrentItem()
        }
    }

    // allow for 100 back cycles from the beginning
    // should be enough to create an illusion of infinity
    // warning: scrolling to very high values (1,000,000+) results in
    // strange drawing behaviour
    private val offsetAmount: Int
        get() {
            val adapter = adapter
            if ((adapter?.count ?: 0) == 0) {
                return 0
            }
            return if (adapter is InternalAdapter && adapter.isCircular) {
                // allow for 100 back cycles from the beginning
                // should be enough to create an illusion of infinity
                // warning: scrolling to very high values (1,000,000+) results in
                // strange drawing behaviour
                adapter.realCount * 100
            } else {
                0
            }
        }
}