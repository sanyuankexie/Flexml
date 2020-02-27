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
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.assign.Assignment
import com.guet.flexbox.litho.factories.assign.AttrsAssigns
import com.guet.flexbox.litho.widget.HorizontalScroll
import com.guet.flexbox.litho.widget.HorizontalScrollSpec

internal object ToScroller : ToComponent<Component.Builder<*>>() {

    override val attrsAssigns by AttrsAssigns
            .create(CommonAssigns.attrsAssigns) {
                register("scrollBarEnable", object : Assignment<Component.Builder<*>, Boolean> {
                    override fun assign(c: Component.Builder<*>, display: Boolean, other: Map<String, Any>, value: Boolean) {
                        if (c is HorizontalScroll.Builder) {
                            c.scrollbarEnabled(value)
                        } else if (c is VerticalScroll.Builder) {
                            c.scrollbarEnabled(value)
                        }
                    }
                })
                register("fillViewport", object : Assignment<Component.Builder<*>, Boolean> {
                    override fun assign(c: Component.Builder<*>, display: Boolean, other: Map<String, Any>, value: Boolean) {
                        if (c is HorizontalScroll.Builder) {
                            c.fillViewport(value)
                        } else if (c is VerticalScroll.Builder) {
                            c.fillViewport(value)
                        }
                    }
                })
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
}