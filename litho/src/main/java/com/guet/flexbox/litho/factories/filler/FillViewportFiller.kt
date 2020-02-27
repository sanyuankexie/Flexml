package com.guet.flexbox.litho.factories.filler

import com.facebook.litho.Component
import com.facebook.litho.widget.VerticalScroll
import com.guet.flexbox.litho.widget.HorizontalScroll

internal object FillViewportFiller : PropFiller<Component.Builder<*>, Boolean> {
    override fun fill(
            c: Component.Builder<*>,
            display: Boolean,
            other: Map<String, Any>,
            value: Boolean
    ) {
        if (c is HorizontalScroll.Builder) {
            c.fillViewport(value)
        } else if (c is VerticalScroll.Builder) {
            c.fillViewport(value)
        }
    }
}