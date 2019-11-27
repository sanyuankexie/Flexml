package com.guet.flexbox.build

import android.view.View
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent
import com.guet.flexbox.el.PropsELContext

internal object EmptyFactory : WidgetFactory<EmptyComponent.Builder>() {

    override fun onCreateWidget(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }

    override fun calculateVisibility(
            c: ComponentContext,
            pager: PropsELContext,
            attrs: Map<String, String>?,
            upperVisibility: Int): Int {
        val value = super.calculateVisibility(c, pager, attrs, upperVisibility)
        return if (value == View.VISIBLE) {
            View.INVISIBLE
        } else {
            value
        }
    }
}