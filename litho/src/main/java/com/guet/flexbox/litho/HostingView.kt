package com.guet.flexbox.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.guet.flexbox.HttpClient

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    internal val target = EventTarget(this)

    internal var httpClient: HttpClient? = null

    internal var pageEventListener: PageEventListener? = null

    fun setPageEventListener(pageEventListener: PageEventListener?) {
        this.pageEventListener = pageEventListener
    }

    fun setHttpClient(httpClient: HttpClient?) {
        this.httpClient = httpClient
    }

    var templatePage: TemplatePage?
        set(value) {
            templatePage?.eventBridge?.target = null
            value?.eventBridge?.target = target
            super.setComponentTree(value)
        }
        get() {
            return super.getComponentTree() as? TemplatePage
        }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    override fun getComponentTree(): ComponentTree? {
        return super.getComponentTree()
    }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    override fun setComponentTree(componentTree: ComponentTree?) {
        throw IllegalStateException()
    }

    interface PageEventListener {
        fun onEventDispatched(
                h: HostingView,
                source: View?,
                values: Array<out Any?>?
        )
    }

}