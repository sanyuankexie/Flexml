package com.guet.flexbox

import android.content.Context
import android.util.AttributeSet
import com.facebook.litho.LithoView
import com.facebook.litho.ThreadUtils
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.el.PropsELContext

class PageHostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    private val pageContext = object : PageContext() {
        override fun send(key: String, vararg data: Any) {
            eventHandler?.handleEvent(this@PageHostView, key, data)
        }
    }

    var eventHandler: EventHandler? = null

    fun setContentAsync(c: RenderContent) {
        ThreadUtils.assertMainThread()
        val elContext = PropsELContext(c.data, pageContext)
        setComponentAsync(Host.create(componentContext)
                .content(c.content)
                .elContext(elContext)
                .build())
    }

    interface EventHandler {
        fun handleEvent(v: PageHostView, key: String, value: Any)
    }
}