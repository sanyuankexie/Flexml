package com.guet.flexbox.build

import android.view.View
import com.facebook.litho.widget.EmptyComponent

internal object EmptyFactory : WidgetFactory<EmptyComponent.Builder>() {
    override fun onCreateWidget(
            c: BuildContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c.componentContext)
    }

    override fun calculateVisibility(
            c: BuildContext,
            attrs: Map<String, String>?,
            upperVisibility: Int): Int {
        val value = super.calculateVisibility(c, attrs, upperVisibility)
        return if (value == View.VISIBLE) {
            View.INVISIBLE
        } else {
            value
        }
    }
}