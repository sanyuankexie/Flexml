package com.guet.flexbox.litho.factories.filler

import com.bumptech.glide.Glide
import com.guet.flexbox.litho.widget.DynamicImage

internal object GlideModelFiller : PropFiller<DynamicImage.Builder, Any> {
    override fun fill(
            c: DynamicImage.Builder,
            display: Boolean,
            other: Map<String, Any>,
            value: Any
    ) {
        Glide.with(c.context!!.androidContext)
                .load(value)
                .preload()
        c.model(value)
    }
}