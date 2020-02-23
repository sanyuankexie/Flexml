package com.guet.flexbox.litho.factories

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.widget.NestedScrollView
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.VerticalScroll
import com.facebook.litho.widget.VerticalScrollSpec
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.ChildComponent
import com.guet.flexbox.litho.resolve.createProvider
import com.guet.flexbox.litho.widget.HorizontalScroll
import com.guet.flexbox.litho.widget.HorizontalScrollSpec

internal object ToScroller : ToComponent<Component.Builder<*>>(CommonAssigns) {

    override val matcherProvider = createProvider<Component.Builder<*>> {
        register("scrollBarEnable") { _, _, value: Boolean ->
            if (this is HorizontalScroll.Builder) {
                scrollbarEnabled(value)
            } else if (this is VerticalScroll.Builder) {
                scrollbarEnabled(value)
            }
        }
        register("fillViewport") { _, _, value: Boolean ->
            if (this is HorizontalScroll.Builder) {
                fillViewport(value)
            } else if (this is VerticalScroll.Builder) {
                fillViewport(value)
            }
        }
    }

    private object TouchInterceptHandler : HorizontalScrollSpec.OnInterceptTouchListener,
            VerticalScrollSpec.OnInterceptTouchListener {

        private fun onInterceptTouch(view: ViewGroup): Boolean {
            view.requestDisallowInterceptTouchEvent(true)
            return true
        }

        override fun onInterceptTouch(nestedScrollView: HorizontalScrollView, event: MotionEvent?): Boolean {
            return onInterceptTouch(nestedScrollView)
        }

        override fun onInterceptTouch(nestedScrollView: NestedScrollView, event: MotionEvent?): Boolean {
            return onInterceptTouch(nestedScrollView)
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component.Builder<*> {
        return when (attrs.getOrElse("orientation") { Orientation.VERTICAL }) {
            Orientation.HORIZONTAL -> {
                HorizontalScroll.create(c).apply {
                    onInterceptTouchListener(TouchInterceptHandler)
                }
            }
            else -> {
                VerticalScroll.create(c).apply {
                    onInterceptTouchListener(TouchInterceptHandler)
                }
            }
        }
    }

    override fun onInstallChildren(
            owner: Component.Builder<*>,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
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
}