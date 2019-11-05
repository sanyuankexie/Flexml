package com.guet.flexbox.build

import com.facebook.litho.widget.EmptyComponent

internal object EmptyFactory : WidgetFactory<EmptyComponent.Builder>() {
    override fun create(
            c: BuildContext,
            attrs: Map<String, String>
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c.componentContext)
    }
}