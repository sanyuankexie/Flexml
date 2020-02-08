package com.guet.flexbox.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RestrictTo
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.guet.flexbox.HttpClient
import com.guet.flexbox.litho.event.EventTarget

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    internal val target = EventTarget(this)

    var httpClient: HttpClient? = null

    var pageEventListener: PageEventListener? = null

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
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun getComponentTree(): ComponentTree? {
        return super.getComponentTree()
    }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun setComponentTree(componentTree: ComponentTree?) {
        super.setComponentTree(componentTree)
    }

    interface PageEventListener {
        fun onEventDispatched(
                h: HostingView,
                source: View?,
                values: Array<out Any?>?
        )
    }

}