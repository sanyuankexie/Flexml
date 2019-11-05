package com.guet.flexbox.build

import com.facebook.litho.widget.EmptyComponent

internal object EmptyFactory : WidgetFactory<EmptyComponent.Builder>() {
    override fun onCreate(
            c: BuildContext,
            attrs: Map<String, String>,
            visibility: Int
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c.componentContext)
    }
}