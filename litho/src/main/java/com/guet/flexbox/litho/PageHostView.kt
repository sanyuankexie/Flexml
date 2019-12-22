package com.guet.flexbox.litho

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import com.facebook.litho.*
import com.facebook.litho.config.ComponentsConfiguration
import com.guet.flexbox.PageContext
import com.guet.flexbox.content.DynamicNode
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.databinding.DataBindingUtils
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.widget.EventHandler

class PageHostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    init {
        val lithoCompat = object : ComponentTree.MeasureListener, OnDirtyMountListener {

            private val rect = Rect(0, 0, measuredWidth, measuredHeight)

            override fun onSetRootAndSizeSpec(width: Int, height: Int) {
                if (height > 0) {
                    post {
                        if (measuredHeight == 0) {
                            layoutParams?.let {
                                it.width = width
                                forceRelayout()
                            }
                        }
                    }
                }
            }

            override fun onDirtyMount(view: LithoView) {
                view.performIncrementalMount(
                        rect.apply {
                            set(0, 0, measuredWidth, measuredHeight)
                        }, false
                )
            }
        }
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .layoutThreadHandler(WorkThreadHandler)
                .measureListener(lithoCompat)
                .build()
        setOnDirtyMountListener(lithoCompat)
    }


    private val pageContext = object : PageContext() {
        override fun send(key: String, vararg data: Any) {
            eventHandler?.handleEvent(this@PageHostView, key, data)
        }
    }

    override fun setLayoutParams(params: LayoutParams?) {
        check(params?.width != LayoutParams.WRAP_CONTENT) { "width forbid wrap_content" }
        super.setLayoutParams(params)
    }

    var eventHandler: EventHandler? = null

    @JvmOverloads
    fun setContentAsync(node: DynamicNode, data: Any?, tag: String? = null) {
        val c = componentContext
        WorkThreadHandler.post {
            val elContext = PropsELContext(data, pageContext)
            val render = DataBindingUtils.bindNode(c.androidContext, node, elContext).single()
            setContentTryAsync(render, elContext, tag)
        }
    }

    @JvmOverloads
    fun setContentAsync(c: RenderContent, tag: String? = null) {
        ThreadUtils.assertMainThread()
        setContentTryAsync(c.content, PropsELContext(c.data, pageContext), tag)
    }

    private fun setContentTryAsync(n: RenderNode, data: ELContext, tag: String? = null) {
        componentTree?.setRootAndSizeSpecAsync(PageHost.create(componentContext)
                .content(n)
                .tag(tag)
                .elContext(data)
                .build(),
                SizeSpec.makeSizeSpec(measuredWidth, SizeSpec.EXACTLY),
                when (layoutParams?.height) {
                    LayoutParams.WRAP_CONTENT -> SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
                    else -> SizeSpec.makeSizeSpec(measuredHeight, SizeSpec.EXACTLY)
                })
    }


    private companion object WorkThreadHandler : Handler({
        val thread = HandlerThread("WorkThreadHandler")
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

        override fun isTracing(): Boolean = false

        override fun remove(runnable: Runnable) {
            removeCallbacks(runnable)
        }
    }

}