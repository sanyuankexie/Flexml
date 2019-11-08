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

    override fun calculateOwnerVisibility(
            c: BuildContext,
            attrs: Map<String, String>?,
            upperVisibility: Int): Int {
        return if (upperVisibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            upperVisibility
        }
    }
}