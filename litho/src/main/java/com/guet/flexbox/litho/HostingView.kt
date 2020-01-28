package com.guet.flexbox.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import com.facebook.litho.*
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.EventBridge
import com.guet.flexbox.HttpClient
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    internal val pageContext = HostContextImpl(this)

    internal var hostingPage: Page? = null

    internal var httpClient: HttpClient? = null

    internal var pageEventListener: PageEventListener? = null

    init {
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .layoutThreadHandler(LayoutThreadHandler)
                .build()
    }

    fun setPageEventListener(pageEventListener: PageEventListener?) {
        this.pageEventListener = pageEventListener
    }

    fun setHttpClient(httpClient: HttpClient?) {
        this.httpClient = httpClient
    }

    override fun setLayoutParams(params: LayoutParams?) {
        check(params?.width != LayoutParams.WRAP_CONTENT) { "width forbid wrap_content" }
        super.setLayoutParams(params)
    }

    @MainThread
    fun setContentAsync(page: Page?) {
        ThreadUtils.assertMainThread()
        this.hostingPage?.event?.target = null
        if (page == null) {
            return
        }
        this.hostingPage = page
        page.event.target = pageContext
        componentTree?.setRootAndSizeSpecAsync(page.display,
                SizeSpec.makeSizeSpec(measuredWidth, SizeSpec.EXACTLY),
                when (layoutParams?.height) {
                    LayoutParams.WRAP_CONTENT -> SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
                    else -> SizeSpec.makeSizeSpec(measuredHeight, SizeSpec.EXACTLY)
                })
    }

    @MainThread
    fun setContentAsync(node: TemplateNode, data: Any?) {
        ThreadUtils.assertMainThread()
        val elContext = PropsELContext(data)
        val c = componentContext
        AppExecutors.runOnAsyncThread {
            val component = LithoBuildTool.build(
                    node,
                    pageContext,
                    elContext,
                    c
            ) as Component
            val page = Page(node, component,
                    EventBridge().apply {
                        target = pageContext
                    })
            post {
                this.hostingPage?.event?.target = null
                this.hostingPage = page
                val tree = componentTree ?: return@post
                tree.setRootAndSizeSpecAsync(
                        component,
                        SizeSpec.makeSizeSpec(measuredWidth, SizeSpec.EXACTLY),
                        when (layoutParams?.width ?: 0) {
                            LayoutParams.WRAP_CONTENT ->
                                SizeSpec.makeSizeSpec(
                                        0,
                                        SizeSpec.UNSPECIFIED
                                )
                            else ->
                                SizeSpec.makeSizeSpec(
                                        measuredHeight,
                                        SizeSpec.EXACTLY
                                )
                        })
            }
        }
    }

    interface PageEventListener {
        fun onEventDispatched(
                h: HostingView,
                source: View?,
                values: Array<out Any?>?
        )
    }

}