package com.guet.flexbox.litho

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent

internal object ToEmpty : ToComponent<EmptyComponent.Builder>(Common) {
    override val attributeSet: AttributeSet<EmptyComponent.Builder>
        get() = emptyMap()

    override fun create(c: ComponentContext, visibility: Boolean, attrs: Map<String, Any>): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}