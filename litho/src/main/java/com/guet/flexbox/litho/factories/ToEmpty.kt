package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.facebook.litho.widget.EmptyComponent
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.factories.filler.PropsFiller

internal object ToEmpty : ToComponent<EmptyComponent.Builder>() {
    override val propsFiller = PropsFiller
            .use<EmptyComponent.Builder>(CommonProps)

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): EmptyComponent.Builder {
        return EmptyComponent.create(c)
    }
}