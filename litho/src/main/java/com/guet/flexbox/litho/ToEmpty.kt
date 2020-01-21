package com.guet.flexbox.litho

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent
import com.guet.flexbox.build.AttributeSet

internal object ToEmpty : ToComponent<EmptyComponent.Builder>(Common) {
    override val attributeAssignSet: AttributeAssignSet<EmptyComponent.Builder>
        get() = emptyMap()

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}