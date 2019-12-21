package com.guet.flexbox

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.facebook.litho.SizeSpec
import com.facebook.litho.ThreadUtils
import com.guet.flexbox.content.RenderContent
import com.guet.flexbox.el.PropsELContext

class PageHostView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    init {
        componentTree = ComponentTree.create(componentContext)
                .isReconciliationEnabled(false)
                .measureListener { width, height ->
                    Log.i("@@@", "width$width+$height+${layoutParams.width}+${layoutParams.height}")
                }
                .build()
    }

    private val pageContext = object : PageContext() {
        override fun send(key: String, vararg data: Any) {
            eventHandler?.handleEvent(this@PageHostView, key, data)
        }
    }

    var eventHandler: EventHandler? = null

    @JvmOverloads
    fun setContent(
            c: RenderContent,
            async: Boolean = true,
            widthSpec: Int = SizeSpec.makeSizeSpec(measuredWidth, SizeSpec.EXACTLY),
            heightSpec: Int = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)) {
        ThreadUtils.assertMainThread()
        val root = PageHost.create(componentContext)
                .content(c.content)
                .elContext(PropsELContext(c.data, pageContext))
                .build()
        if (async) {
            componentTree?.setRootAndSizeSpecAsync(
                    root,
                    widthSpec,
                    heightSpec
            )
        } else {
            componentTree?.setRootAndSizeSpec(
                    root,
                    widthSpec,
                    heightSpec
            )
        }
    }

    interface EventHandler {
        fun handleEvent(v: View, key: String, value: Any)
    }
}