package com.guet.flexbox.build

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent

internal object EmptyAdapt : ComponentAdapt<EmptyComponent.Builder>(CommonAdapt) {
    override val attributeSet: AttributeSet<EmptyComponent.Builder>
        get() = emptyMap()

    override fun onCreate(c: ComponentContext, type: String, visibility: Boolean, attrs: Map<String, Any>): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}