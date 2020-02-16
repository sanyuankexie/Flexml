package com.guet.flexbox.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RestrictTo
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.facebook.litho.SizeSpec
import com.guet.flexbox.HttpClient
import com.guet.flexbox.litho.event.EventTarget

class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    init {
        super.setClipChildren(false)
    }

    override fun setClipChildren(clipChildren: Boolean) {
    }

    internal val target = EventTarget(this)

    var httpClient: HttpClient? = null

    var pageEventListener: PageEventListener? = null

    var templatePage: TemplatePage?
        set(value) {
            templatePage?.eventTarget = null
            value?.eventTarget = target
            super.setComponentTree(value)
            requestLayout()
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
    override fun getComponentTree(): TemplatePage? {
        return super.getComponentTree() as TemplatePage?
    }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun setComponentTree(componentTree: ComponentTree?) {
        super.setComponentTree(componentTree as TemplatePage?)
    }

    override fun setClipToPadding(clipToPadding: Boolean) {
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {}

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {}

    override fun getPaddingLeft(): Int {
        return 0
    }

    override fun getPaddingBottom(): Int {
        return 0
    }

    override fun getPaddingEnd(): Int {
        return 0
    }

    override fun getPaddingRight(): Int {
        return 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val page = templatePage
        if (page != null) {
            //fast path
            super.onMeasure(
                    SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED),
                    SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)
            )
            setMeasuredDimension(
                    View.getDefaultSize(
                            page.width,
                            widthMeasureSpec
                    ),
                    View.getDefaultSize(
                            page.height,
                            heightMeasureSpec
                    )
            )
        } else {
            setMeasuredDimension(
                    View.getDefaultSize(
                            suggestedMinimumWidth,
                            widthMeasureSpec
                    ),
                    View.getDefaultSize(
                            suggestedMinimumHeight,
                            heightMeasureSpec
                    )
            )
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