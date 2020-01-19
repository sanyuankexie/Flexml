package com.guet.flexbox.litho

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import com.facebook.litho.*
import com.guet.flexbox.*
import com.guet.flexbox.el.ELContext
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
        override fun commit(): (ELContext) -> Unit {
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
                actions.forEach {
                    it.invoke(elContext)
                }
                val node = template
                val tree = componentTree
                if (tree != null && node != null) {
                    val c = componentContext
                    val height = layoutParams?.width ?: 0
                    val mH = measuredHeight
                    val mW = measuredWidth
                    ConcurrentUtils.runOnAsyncThread {
                        val context = ForwardContext()
                                .apply {
                                    target = pageContext
                                }
                        val component = LithoBuildUtils.bindNode(
                                node,
                                context,
                                elContext,
                                true,
                                c
                        ).single() as Component
                        tree.setRootAndSizeSpec(
                                component,
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
                        ConcurrentUtils.runOnUiThread {
                            _pageEventListener?.onPageChanged(
                                    this@HostingView,
                                    Page(node, component, context)
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
        override fun commit(): (ELContext) -> Unit {
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
                            ConcurrentUtils.runOnUiThread {
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
                            ConcurrentUtils.runOnUiThread {
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
                .layoutThreadHandler(LayoutThreadHandler)
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
            elContext: PropsELContext
    ) {
        val tree = componentTree
        if (tree != null) {
            val c = componentContext
            val height = layoutParams?.width ?: 0
            val mH = measuredHeight
            val mW = measuredWidth
            ConcurrentUtils.runOnAsyncThread {
                val component = LithoBuildUtils.bindNode(
                        node,
                        pageContext,
                        elContext,
                        true,
                        c
                ).single() as Component
                tree.setRootAndSizeSpec(
                        component,
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
                page: Page
        )
    }

}