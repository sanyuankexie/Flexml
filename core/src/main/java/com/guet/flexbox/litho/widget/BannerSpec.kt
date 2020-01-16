package com.guet.flexbox.litho.widget

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.Row
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.Prop
import com.facebook.litho.annotations.PropDefault
import com.facebook.litho.widget.*

@LayoutSpec
object BannerSpec {


    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    private val mainThread = Handler(Looper.getMainLooper())

    private val emptyCallback = object : ChangeSetCompleteCallback {

        override fun onDataBound() {

        }

        override fun onDataRendered(
                isMounted: Boolean,
                uptimeMillis: Long
        ) {

        }
    }

    @OnCreateLayout
    fun onCreateLayout(
            c: ComponentContext,
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true) wrapContent: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component {
        if (children.isNullOrEmpty()) {
            return EmptyComponent.create(c).build()
        }
        val target = RecyclerBinder.Builder()
                .layoutInfo(LinearLayoutInfo(
                        c,
                        LinearLayoutManager.HORIZONTAL,
                        false
                ))
                .canMeasure(true)
                .wrapContent(wrapContent)
                .isCircular(isCircular)
                .build(c)
        val cc = children.map {
            ComponentRenderInfo.create()
                    .component(Row.create(c)
                            .flexGrow(1f)
                            .child(it)
                    ).build()
        }
        target.insertRangeAtAsync(0, cc)
        target.notifyChangeSetCompleteAsync(
                true,
                emptyCallback
        )
        return Recycler.create(c)
                .binder(when {
                    timeSpan > 0 -> BinderWrapper(target, timeSpan)
                    else -> target
                })
                .snapHelper(PagerSnapHelper())
                .build()
    }

    class BinderWrapper(
            private val target: RecyclerBinder,
            private val timeSpan: Long
    ) : Binder<RecyclerView> by target, Runnable {

        override fun run() {
            var pos = target.findFirstFullyVisibleItemPosition()
            if (pos == RecyclerView.NO_POSITION) {
                pos = target.findFirstVisibleItemPosition()
            }
            if (pos != RecyclerView.NO_POSITION) {
                target.scrollSmoothToPosition(
                        pos,
                        1,
                        SmoothScrollAlignmentType.SNAP_TO_CENTER
                )
            }
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