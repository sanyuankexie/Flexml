package com.guet.flexbox.build

import android.view.View
import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent

internal object EmptyFactory : WidgetFactory<EmptyComponent.Builder>() {
    override fun onCreateWidget(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            visibility: Int
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }

    override fun calculateVisibility(
            dataBinding: DataContext,
            attrs: Map<String, String>?,
            upperVisibility: Int): Int {
        val value = super.calculateVisibility(dataBinding, attrs, upperVisibility)
        return if (value == View.VISIBLE) {
            View.INVISIBLE
        } else {
            value
        }
    }
}