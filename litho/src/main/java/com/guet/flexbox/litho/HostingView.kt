package com.guet.flexbox.litho

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RestrictTo
import com.facebook.litho.ComponentTree
import com.facebook.litho.LithoView
import com.facebook.litho.SizeSpec
import com.guet.flexbox.transaction.ActionKey
import com.guet.flexbox.transaction.action.ActionTarget
import com.guet.flexbox.transaction.action.HttpAction
import com.guet.flexbox.transaction.action.HttpClient


open class HostingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LithoView(context, attrs) {

    init {
        super.suppressMeasureComponentTree(true)
    }

    internal val target = ActionTargetImpl()

    var httpClient: HttpClient? = null

    var pageEventListener: PageEventListener? = null

    var templatePage: TemplatePage?
        set(value) {
            templatePage?.actionTarget = null
            value?.actionTarget = target
            super.setComponentTree(value)
            requestLayout()
        }
        get() {
            return super.getComponentTree() as? TemplatePage
        }

    override fun setClipChildren(clipChildren: Boolean) {
    }

    @Deprecated("", level = DeprecationLevel.HIDDEN)
    final override fun suppressMeasureComponentTree(suppress: Boolean) {
    }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final override fun getComponentTree(): TemplatePage? {
        return super.getComponentTree() as TemplatePage?
    }

    @Deprecated(
            "use templatePage",
            ReplaceWith("templatePage"),
            DeprecationLevel.HIDDEN
    )
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    final override fun setComponentTree(componentTree: ComponentTree?) {
        super.setComponentTree(componentTree as TemplatePage?)
    }

    final override fun setClipToPadding(clipToPadding: Boolean) {
    }

    final override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {}

    final override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {}

    final override fun getPaddingLeft(): Int {
        return 0
    }

    final override fun getPaddingBottom(): Int {
        return 0
    }

    final override fun getPaddingEnd(): Int {
        return 0
    }

    final override fun getPaddingRight(): Int {
        return 0
    }

    final override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
            //otherwise
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

    internal inner class ActionTargetImpl : ActionTarget {

        override fun dispatchActions(
                key: ActionKey,
                source: View?,
                args: Array<out Any?>?
        ) {
            when (key) {
                ActionKey.SendObjects -> {
                    pageEventListener?.onEventDispatched(
                            this@HostingView,
                            source,
                            args
                    )
                }
                ActionKey.RefreshPage -> {
                    templatePage?.computeNewLayout()
                }
                ActionKey.HttpRequest -> {
                    httpClient?.enqueue(args!![0] as HttpAction)
                }
            }
        }
    }

}