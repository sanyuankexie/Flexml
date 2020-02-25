package com.guet.flexbox.litho.factories

import com.bumptech.glide.Glide
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.resolve.Assignment
import com.guet.flexbox.litho.resolve.AttrsAssigns
import com.guet.flexbox.litho.widget.DynamicImage

internal object ToDynamicImage : ToComponent<DynamicImage.Builder>() {

    override val attrsAssigns by AttrsAssigns
            .create<DynamicImage.Builder>(CommonAssigns.attrsAssigns) {
                enum("scaleType", DynamicImage.Builder::scaleType)
                value("blurRadius", DynamicImage.Builder::blurRadius)
                value("blurSampling", DynamicImage.Builder::blurSampling)
                value("aspectRatio", DynamicImage.Builder::imageAspectRatio)
                pt("borderLeftTopRadius", DynamicImage.Builder::leftTopRadius)
                pt("borderRightTopRadius", DynamicImage.Builder::rightTopRadius)
                pt("borderRightBottomRadius", DynamicImage.Builder::rightBottomRadius)
                pt("borderLeftBottomRadius", DynamicImage.Builder::leftBottomRadius)
                register("src", object : Assignment<DynamicImage.Builder, Any> {
                    override fun assign(c: DynamicImage.Builder,
                                        display: Boolean,
                                        other: Map<String, Any>,
                                        value: Any) {
                        Glide.with(c.context!!.androidContext)
                                .load(value)
                                .preload()
                        c.model(value)
                    }
                })
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): DynamicImage.Builder {
        return DynamicImage.create(c)
    }
}