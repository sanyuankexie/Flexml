package com.guet.flexbox.build

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent
import com.guet.flexbox.content.RenderNode

internal object Empty : Widget<EmptyComponent.Builder>(Common) {
    override val attributeSet: AttributeSet<EmptyComponent.Builder>
        get() = emptyMap()

    override fun onCreate(c: ComponentContext, renderNode: RenderNode): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}