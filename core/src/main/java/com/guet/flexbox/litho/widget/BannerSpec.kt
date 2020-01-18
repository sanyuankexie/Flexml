package com.guet.flexbox.litho.widget

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.StateValue
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.*
import com.guet.flexbox.litho.concurrent.AsyncThread


@LayoutSpec
object BannerSpec {

    @PropDefault
    val timeSpan = 3000L

    @get:JvmName(name = "getIsCircular")
    @PropDefault
    val isCircular: Boolean = true

    private val needRecreated by lazy<RecyclerBinder.() -> Boolean> {
        val isCircularField = RecyclerBinder::class.java
                .getDeclaredField("mIsCircular")
                .apply {
                    isAccessible = true
                }
        val componentTreeHoldersField = RecyclerBinder::class.java
                .getDeclaredField("mComponentTreeHolders")
                .apply {
                    isAccessible = true
                }
        return@lazy {
            val oldIsCircular = isCircularField.getBoolean(this)
            val count = componentTreeHoldersField.get(this)
                    .run {
                        this as? List<*>
                    }?.size ?: 0
            oldIsCircular && count != 0
        }
    }

    private val emptyCallback = object : ChangeSetCompleteCallback {
        override fun onDataBound() {

        }

        override fun onDataRendered(isMounted: Boolean, uptimeMillis: Long) {
        }
    }

    @OnCreateInitialState
    fun onCreateInitialState(
            c: ComponentContext,
            @Prop(optional = true) isCircular: Boolean,
            binder: StateValue<RecyclerBinder>) {
        binder.set(RecyclerBinder.Builder()
                .isCircular(isCircular)
                .layoutInfo(LinearLayoutInfo(
                        c,
                        RecyclerView.HORIZONTAL,
                        false
                ))
                .asyncInsertLayoutHandler(AsyncThread)
                .build(c))
    }

    @OnCreateLayout
    fun onCreateLayout(
            c: ComponentContext,
            @State(canUpdateLazily = true) binder: RecyclerBinder,
            @Prop(optional = true) timeSpan: Long,
            @Prop(optional = true) isCircular: Boolean,
            @Prop(optional = true, varArg = "child") children: List<Component>?
    ): Component? {
        if (children.isNullOrEmpty()) {
            return null
        }
        val target = if (binder.needRecreated()) {
            val out = StateValue<RecyclerBinder>()
            onCreateInitialState(c, isCircular, out)
            out.get()!!.apply {
                Banner.replaceRecyclerBinder(c, this)
            }
        } else {
            binder
        }
        target.clearAsync()
        target.insertRangeAtAsync(
                0,
                children.map {
                    ComponentRenderInfo.create()
                            .component(it)
                            .build()
                }
        )
        target.notifyChangeSetCompleteAsync(
                true,
                emptyCallback
        )
        return Recycler.create(c)
                .binder(target)
                .snapHelper(PagerSnapHelper())
                .build()
    }

    @OnUpdateState
    fun replaceRecyclerBinder(
            @Param newBinder: RecyclerBinder,
            binder: StateValue<RecyclerBinder>
    ) {
        binder.set(newBinder)
    }
}