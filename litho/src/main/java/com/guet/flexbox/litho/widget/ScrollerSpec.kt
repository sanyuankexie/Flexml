package com.guet.flexbox.litho.widget

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.StateValue
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.*

@LayoutSpec(isPureRender = true)
object ScrollerSpec {

    @PropDefault
    val orientation: Int = RecyclerView.VERTICAL

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

    private val callback = object : ChangeSetCompleteCallback {
        override fun onDataBound() {
        }

        override fun onDataRendered(isMounted: Boolean, uptimeMillis: Long) {

        }
    }

    @OnCreateLayout
    fun onCreateLayout(
            c: ComponentContext,
            @Prop component: Component,
            @Prop(optional = true) orientation: Int,
            @State layoutManager: LinearLayoutManager,
            @State binder: RecyclerBinder
    ): Component {
        layoutManager.orientation = orientation
        binder.clearAsync()
        binder.insertItemAtAsync(0, ComponentRenderInfo.create()
                .component(component)
                .isFullSpan(true)
                .build())
        binder.notifyChangeSetCompleteAsync(true, callback)
        return Recycler.create(c)
                .hasFixedSize(true)
                .binder(binder)
                .build()
    }
}