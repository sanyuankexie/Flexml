package com.guet.flexbox

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import com.facebook.litho.*
import com.facebook.litho.config.ComponentsConfiguration
import com.guet.flexbox.el.PropsELContext

class PageHostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    private val pageContext = object : PageContext {
        override fun send(key: String, vararg data: Any) {
            _eventHandler?.handleEvent(this@PageHostView, key, data)
        }
    }

    private var _onDirtyMountListener: OnDirtyMountListener? = null

    private var _eventHandler: EventHandler? = null

    private var data: Any? = null

    private var template: TemplateNode? = null

    init {
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .layoutThreadHandler(WorkerThreadHandler)
                .build()
        super.setOnDirtyMountListener { view ->
            this.performIncrementalMount(
                    Rect(0, 0, measuredWidth, measuredHeight), false
            )
            _onDirtyMountListener?.onDirtyMount(view)
        }
    }

    fun setEventHandler(eventHandler: EventHandler) {
        _eventHandler = eventHandler
    }

    override fun setOnDirtyMountListener(onDirtyMountListener: OnDirtyMountListener?) {
        _onDirtyMountListener = onDirtyMountListener
    }

    override fun setLayoutParams(params: LayoutParams?) {
        check(params?.width != LayoutParams.WRAP_CONTENT) { "width forbid wrap_content" }
        super.setLayoutParams(params)
    }

    @MainThread
    fun setContentAsync(preloadPage: PreloadPage) {
        ThreadUtils.assertMainThread()
        preloadPage.exposed.target = pageContext
        template = preloadPage.template
        data = preloadPage.data
        componentTree?.setRootAndSizeSpecAsync(preloadPage.component,
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
        WorkerThreadHandler.post {
            val elContext = PropsELContext(data, pageContext)
            val component = PageUtils.bindNode(c, node, elContext).single()
            tree.setRootAndSizeSpec(component,
                    SizeSpec.makeSizeSpec(mW, SizeSpec.EXACTLY),
                    when (height) {
                        LayoutParams.WRAP_CONTENT -> SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
                        else -> SizeSpec.makeSizeSpec(mH, SizeSpec.EXACTLY)
                    })
        }
    }

    interface EventHandler {
        fun handleEvent(v: View, key: String, value: Array<out Any>)
    }

    private companion object WorkerThreadHandler : Handler({
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