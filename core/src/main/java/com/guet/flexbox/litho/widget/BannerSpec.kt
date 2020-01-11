package com.guet.flexbox.litho.widget

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.annotations.PropDefault
import com.facebook.litho.widget.*

@LayoutSpec
object BannerSpec {

    @PropDefault
    val timeSpan = 2000L

    private val mainThread = Handler(Looper.getMainLooper())

    @OnCreateLayout
    fun onCreateLayoutWithSizeSpec(
            c: ComponentContext,
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component {
        if (children.isNullOrEmpty()) {
            return EmptyComponent.create(c).build()
        }
        val binder = RecyclerBinder.Builder()
                .layoutInfo(LinearLayoutInfo(
                        c,
                        LinearLayoutManager.HORIZONTAL,
                        false
                ))
                .isCircular(isCircular)
                .build(c)
                .apply {
                    insertRangeAtAsync(0, children.map {
                        ComponentRenderInfo.create()
                                .component(it)
                                .build()
                    })
                }.run {
                    if (timeSpan > 0) {
                        CarouselWrapper(this, timeSpan)
                    } else {
                        this
                    }
                }
        return Recycler.create(c)
                .binder(binder)
                .snapHelper(PagerSnapHelper())
                .build()
    }

    private class CarouselWrapper(
            private val target: RecyclerBinder,
            private val timeSpan: Long
    ) : Binder<RecyclerView> by target, Runnable {

        override fun run() {
            target.scrollSmoothToPosition(
                    target.findFirstVisibleItemPosition(),
                    1,
                    SmoothScrollAlignmentType.SNAP_TO_CENTER
            )
            mainThread.postDelayed(this, timeSpan)
        }

        override fun mount(view: RecyclerView) {
            target.mount(view)
            mainThread.postDelayed(this, timeSpan)
        }

        override fun unmount(view: RecyclerView) {
            target.unmount(view)
            mainThread.removeCallbacks(this)
        }
    }
}