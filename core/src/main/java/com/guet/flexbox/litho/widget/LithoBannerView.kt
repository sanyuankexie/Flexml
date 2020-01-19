package com.guet.flexbox.litho.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.facebook.litho.HasLithoViewChildren
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaJustify
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.litho.toPx
import java.lang.ref.WeakReference

class LithoBannerView(context: Context) : FrameLayout(context), HasLithoViewChildren {

    val viewPager: ViewPager2 = ViewPager2(context)

    val indicators = LithoView(context)

    var indicatorHeightPx: Int = BannerSpec.indicatorHeightPx

    var indicatorSelectedColor: Int = BannerSpec.indicatorSelectedColor

    var indicatorUnselectedColor: Int = BannerSpec.indicatorUnselectedColor

    var indicatorEnable: Boolean = BannerSpec.indicatorEnable

    private var token: Runnable? = null

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
                            .marginPx(YogaEdge.BOTTOM, indicatorHeightPx)
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
                                                    backgroundColor(indicatorSelectedColor)
                                                } else {
                                                    backgroundColor(indicatorUnselectedColor)
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

    fun bind(timeSpan: Long) {
        val rect = Rect(0, 0, measuredWidth, measuredWidth)
        ArrayList<LithoView>(viewPager.childCount + 1).apply {
            add(indicators)
            (0 until viewPager.childCount).forEach {
                val v = viewPager.getChildAt(it) as? LithoView
                if (v != null) {
                    add(v)
                }
            }
        }.forEach {
            it.performIncrementalMount(rect, false)
        }
        if (timeSpan <= 0) {
            return
        }
        token = object : WeakReference<ViewPager2>(viewPager), Runnable {
            override fun run() {
                get()?.let { viewPager ->
                    val adapter = viewPager.adapter
                    val current = viewPager.currentItem
                    if (adapter is BannerAdapter) {
                        val next = if (adapter.isCircular) {
                            current + 1
                        } else {
                            current + 1 % adapter.realCount
                        }
                        viewPager.setCurrentItem(
                                next,
                                true
                        )
                        ConcurrentUtils.mainThreadHandler
                                .postDelayed(this, timeSpan)
                    }
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