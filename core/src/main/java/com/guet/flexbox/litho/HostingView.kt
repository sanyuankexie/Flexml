package com.guet.flexbox.litho

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import androidx.annotation.MainThread
import com.facebook.litho.*
import com.facebook.litho.config.ComponentsConfiguration
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    private val pageContext = object : PageContext() {
        override fun send(key: String, vararg data: Any) {
            _eventListener?.handleEvent(this@HostingView, key, data)
        }
    }

    private var _onDirtyMountListener: OnDirtyMountListener? = null

    private var _eventListener: EventListener? = null

    init {
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .layoutThreadHandler(Companion)
                .build()
        super.setOnDirtyMountListener { view ->
            this.performIncrementalMount(
                    Rect(0, 0, measuredWidth, measuredHeight), false
            )
            _onDirtyMountListener?.onDirtyMount(view)
        }
    }

    fun setEventHandler(eventListener: EventListener) {
        _eventListener = eventListener
    }

    override fun setOnDirtyMountListener(onDirtyMountListener: OnDirtyMountListener?) {
        _onDirtyMountListener = onDirtyMountListener
    }

    override fun setLayoutParams(params: LayoutParams?) {
        check(params?.width != LayoutParams.WRAP_CONTENT) { "width forbid wrap_content" }
        super.setLayoutParams(params)
    }

    @MainThread
    fun setContentAsync(page: Page) {
        ThreadUtils.assertMainThread()
        page.eventBridge.target = pageContext
        componentTree?.setRootAndSizeSpecAsync(page.component,
                SizeSpec.makeSizeSpec(measuredWidth, SizeSpec.EXACTLY),
                when (layoutParams?.height) {
                    LayoutParams.WRAP_CONTENT -> SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
                    else -> SizeSpec.makeSizeSpec(measuredHeight, SizeSpec.EXACTLY)
                })
    }

    @MainThread
    fun setContentAsync(node: TemplateNode, data: Any?) {
        ThreadUtils.assertMainThread()
        val tree = componentTree ?: return
        val c = componentContext
        val height = layoutParams?.width ?: 0
        val mH = measuredHeight
        val mW = measuredWidth
        Companion.post {
            val elContext = PropsELContext(data)
            val component = DefaultBuildUtils.bindNode(
                    node,
                    pageContext,
                    elContext,
                    true,
                    c
            ).single()
            tree.setRootAndSizeSpec(component as Component,
                    SizeSpec.makeSizeSpec(mW, SizeSpec.EXACTLY),
                    when (height) {
                        LayoutParams.WRAP_CONTENT -> SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
                        else -> SizeSpec.makeSizeSpec(mH, SizeSpec.EXACTLY)
                    })
        }
    }

    interface EventListener {
        fun handleEvent(host: HostingView, key: String, value: Array<out Any>)
    }

    private companion object : Handler({
        val thread = HandlerThread("WorkerThread")
        thread.start()
        thread.looper
    }()), LithoHandler {

        init {
            ComponentsConfiguration.incrementalMountWhenNotVisible = true
        }

        override fun post(runnable: Runnable, tag: String?) {
            post(runnable)
        }

        override fun postAtFront(runnable: Runnable, tag: String?) {
            postAtFrontOfQueue(runnable)
        }

        override fun isTracing(): Boolean = true

        override fun remove(runnable: Runnable) {
            removeCallbacks(runnable)
        }
    }

}