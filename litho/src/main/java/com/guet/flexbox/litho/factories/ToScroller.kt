package com.guet.flexbox.litho.factories

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.widget.NestedScrollView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.VerticalScroll
import com.facebook.litho.widget.VerticalScrollSpec
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.filler.FillViewportFiller
import com.guet.flexbox.litho.factories.filler.PropsFiller
import com.guet.flexbox.litho.factories.filler.ScrollBarEnableFiller
import com.guet.flexbox.litho.widget.HorizontalScroll
import com.guet.flexbox.litho.widget.HorizontalScrollSpec

internal object ToScroller : ToComponent<Component.Builder<*>>(),
        HorizontalScrollSpec.OnInterceptTouchListener,
        VerticalScrollSpec.OnInterceptTouchListener {

    override val propsFiller by PropsFiller
            .create(CommonProps) {
                register("scrollBarEnable", ScrollBarEnableFiller)
                register("fillViewport", FillViewportFiller)
            }


    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): Component.Builder<*> {
        return when (attrs.getOrElse("orientation") { Orientation.VERTICAL }) {
            Orientation.HORIZONTAL -> {
                HorizontalScroll.create(c).apply {
                    onInterceptTouchListener(this@ToScroller)
                }
            }
            else -> {
                VerticalScroll.create(c).apply {
                    onInterceptTouchListener(this@ToScroller)
                }
            }
        }
    }

    override fun onInstallChildren(
            owner: Component.Builder<*>,
            visibility: Boolean,
            attrs: PropSet,
            children: List<Widget>
    ) {
        if (children.isNullOrEmpty()) {
            return
        }
        if (owner is HorizontalScroll.Builder) {
            owner.childComponent(children.single())
        } else if (owner is VerticalScroll.Builder) {
            owner.childComponent(children.single())
        }
    }
    
    private fun onInterceptTouchEvent(view: ViewGroup, event: MotionEvent?): Boolean {
        view.requestDisallowInterceptTouchEvent(event?.action == MotionEvent.ACTION_MOVE)
        return when (event?.action) {
            MotionEvent.ACTION_MOVE -> true
            else -> false
        }
    }

    override fun onInterceptTouch(nestedScrollView: HorizontalScrollView, event: MotionEvent?): Boolean {
        return onInterceptTouchEvent(nestedScrollView, event)
    }

    override fun onInterceptTouch(nestedScrollView: NestedScrollView, event: MotionEvent?): Boolean {
        return onInterceptTouchEvent(nestedScrollView, event)
    }
}