package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.factories.assign.AttrsAssigns

internal object ToEmpty : ToComponent<EmptyComponent.Builder>() {
    override val attrsAssigns = AttrsAssigns
            .use<EmptyComponent.Builder>(CommonAssigns.attrsAssigns)

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}