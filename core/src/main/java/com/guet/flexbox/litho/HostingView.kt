package com.guet.flexbox.litho

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import com.facebook.litho.*
import com.facebook.litho.config.ComponentsConfiguration
import com.guet.flexbox.ForwardContext
import com.guet.flexbox.HostingContext
import com.guet.flexbox.HttpClient
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.transaction.HttpTransaction
import com.guet.flexbox.transaction.RefreshTransaction

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    private val pageContext = HostingContextImpl()

    private inner class HostingContextImpl : HostingContext() {

        override fun send(source: View, values: Array<out Any?>) {
            _pageEventListener?.onEventDispatched(
                    this@HostingView,
                    source,
                    values
            )
        }

        override fun http(source: View): HttpTransaction? {
            return HttpTransactionImpl(source)
        }

        override fun refresh(source: View): RefreshTransaction? {
            return RefreshTransactionImpl(source)
        }

    }

    private inner class RefreshTransactionImpl(
            private val source: View
    ) : RefreshTransaction() {
        override fun commit(): (PropsELContext) -> Unit {
            return { elContext ->
                _pageEventListener?.run {
                    sends.forEach {
                        onEventDispatched(
                                this@HostingView,
                                source,
                                it
                        )
                    }
                }
                val node = template
                if (node != null) {
                    actions.forEach {
                        it.invoke(elContext)
                    }
                    setContentAsyncInternal(node, elContext) {
                        post {
                            val newPage = Page(
                                    node,
                                    it,
                                    ForwardContext().apply {
                                        target = pageContext
                                    }
                            )
                            _pageEventListener?.onPageChanged(
                                    this@HostingView,
                                    newPage,
                                    elContext.data
                            )
                        }
                    }
                }
            }
        }
    }

    private inner class HttpTransactionImpl(
            private val source: View
    ) : HttpTransaction() {
        override fun commit(): (PropsELContext) -> Unit {
            return { elContext ->
                _pageEventListener?.run {
                    sends.forEach {
                        onEventDispatched(
                                this@HostingView,
                                source,
                                it
                        )
                    }
                }
                val node = template
                val http = _httpClient
                val success = success
                val error = error
                val url = url
                val method = method
                if (node != null && http != null
                        && url != null && method != null) {
                    val onSuccess: ((Any) -> Unit)? = if (success != null) {
                        {
                            post {
                                success.invoke(
                                        elContext,
                                        pageContext.withView(source),
                                        it
                                )
                            }
                        }
                    } else {
                        null
                    }
                    val onError: (() -> Unit)? = if (error != null) {
                        {
                            post {
                                error.invoke(elContext,
                                        pageContext.withView(source)
                                )
                            }
                        }
                    } else {
                        null
                    }
                    http.enqueue(
                            url,
                            method,
                            prams,
                            onSuccess,
                            onError
                    )
                }
            }
        }
    }

    private var template: TemplateNode? = null

    private var _httpClient: HttpClient? = null

    private var _onDirtyMountListener: OnDirtyMountListener? = null

    private var _pageEventListener: PageEventListener? = null

    init {
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .layoutThreadHandler(Asynchronous)
                .build()
        super.setOnDirtyMountListener { view ->
            this.performIncrementalMount(
                    Rect(0, 0, measuredWidth, measuredHeight), false
            )
            _onDirtyMountListener?.onDirtyMount(view)
        }
    }

    fun setPageEventListener(pageEventListener: PageEventListener?) {
        _pageEventListener = pageEventListener
    }

    fun setHttpClient(httpClient: HttpClient?) {
        _httpClient = httpClient
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
        template = page.template
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
        val elContext = PropsELContext(data)
        template = node
        setContentAsyncInternal(node, elContext)
    }

    private fun setContentAsyncInternal(
            node: TemplateNode,
            elContext: PropsELContext,
            callback: ((Component) -> Unit)? = null
    ) {
        val tree = componentTree
        if (tree != null) {
            val c = componentContext
            val height = layoutParams?.width ?: 0
            val mH = measuredHeight
            val mW = measuredWidth
            Asynchronous.post {
                val component = LithoBuildUtils.bindNode(
                        node,
                        pageContext,
                        elContext,
                        true,
                        c
                ).single()
                tree.setRootAndSizeSpec(
                        component as Component,
                        SizeSpec.makeSizeSpec(mW, SizeSpec.EXACTLY),
                        when (height) {
                            LayoutParams.WRAP_CONTENT ->
                                SizeSpec.makeSizeSpec(
                                        0,
                                        SizeSpec.UNSPECIFIED
                                )
                            else ->
                                SizeSpec.makeSizeSpec(
                                        mH,
                                        SizeSpec.EXACTLY
                                )
                        })
                callback?.invoke(component)
            }
        }
    }

    interface PageEventListener {

        fun onEventDispatched(
                h: HostingView,
                source: View,
                values: Array<out Any?>
        )

        fun onPageChanged(
                h: HostingView,
                page: Page,
                data: Any?
        )
    }

    private companion object Asynchronous : Handler({
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