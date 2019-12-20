package com.guet.flexbox

import android.content.Context
import android.util.AttributeSet
import com.facebook.litho.LithoView
import com.facebook.litho.ThreadUtils
import com.guet.flexbox.content.RenderContent

class PageHostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs), EventHandler {

    var eventListener: EventListener? = null

    fun setContentAsync(c: RenderContent) {
        ThreadUtils.assertMainThread()
        c.bridge.target = this
        setComponentAsync(Renderer.create(componentContext)
                .content(c.content)
                .build())
    }

    override fun handleEvent(key: String, value: Any) {
        eventListener?.onEvent(this, key, value)
    }

    interface EventListener {
        fun onEvent(v: PageHostView, key: String, value: Any)
    }
}