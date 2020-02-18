package com.guet.flexbox.litho.widget

import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.*

@LayoutSpec(isPureRender = true)
object ScrollerSpec {

    @PropDefault
    val orientation: Int = RecyclerView.VERTICAL

    private object Callbacks : ChangeSetCompleteCallback,
            LithoRecylerView.TouchInterceptor {
        override fun onDataBound() {
        }

        override fun onDataRendered(isMounted: Boolean, uptimeMillis: Long) {

        }

        override fun onInterceptTouchEvent(
                recyclerView: RecyclerView,
                ev: MotionEvent
        ): LithoRecylerView.TouchInterceptor.Result {
            recyclerView.requestDisallowInterceptTouchEvent(true)
            return LithoRecylerView.TouchInterceptor.Result.INTERCEPT_TOUCH_EVENT
        }
    }

    @OnCreateInitialState
    fun onCreateInitialState(
            c: ComponentContext,
            binder: StateValue<RecyclerBinder>,
            layoutManager: StateValue<LinearLayoutManager>
    ) {
        val manager = LinearLayoutManager(c.androidContext)
        binder.set(RecyclerBinder.Builder()
                .layoutInfo(LinearLayoutInfo(manager))
                .build(c))
        layoutManager.set(manager)
    }

    @OnCreateLayoutWithSizeSpec
    fun onCreateLayout(
            c: ComponentContext,
            widthSpec: Int,
            heightSpec: Int,
            @Prop component: Component,
            @Prop(optional = true) fillViewport: Boolean,
            @Prop(optional = true) orientation: Int,
            @State layoutManager: LinearLayoutManager,
            @State binder: RecyclerBinder
    ): Component {
        synchronized(layoutManager) {
            layoutManager.orientation = orientation
        }
        val com = if (orientation == RecyclerView.VERTICAL) {
            Row.create(c).widthPx(SizeSpec.getSize(widthSpec))
                    .child(component)
                    .build()
        } else {
            Row.create(c).heightPx(SizeSpec.getSize(heightSpec))
                    .child(component)
                    .build()
        }
        binder.apply {
            clearAsync()
            insertItemAtAsync(0, ComponentRenderInfo.create()
                    .component(com)
                    .isFullSpan(fillViewport)
                    .build()
            )
            notifyChangeSetCompleteAsync(true, Callbacks)
        }
        return GenericRecycler.create(c)
                .touchInterceptor(Callbacks)
                .hasFixedSize(true)
                .binder(binder)
                .build()
    }
}