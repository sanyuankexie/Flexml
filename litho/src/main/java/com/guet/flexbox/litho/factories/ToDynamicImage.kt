package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.factories.filler.GlideModelFiller
import com.guet.flexbox.litho.factories.filler.PropsFiller
import com.guet.flexbox.litho.widget.DynamicImage

internal object ToDynamicImage : ToComponent<DynamicImage.Builder>() {

    override val propsFiller by PropsFiller
            .create<DynamicImage.Builder>(CommonProps) {
                enum("scaleType", DynamicImage.Builder::scaleType)
                value("blurRadius", DynamicImage.Builder::blurRadius)
                value("blurSampling", DynamicImage.Builder::blurSampling)
                value("aspectRatio", DynamicImage.Builder::imageAspectRatio)
                pt("borderLeftTopRadius", DynamicImage.Builder::leftTopRadius)
                pt("borderRightTopRadius", DynamicImage.Builder::rightTopRadius)
                pt("borderRightBottomRadius", DynamicImage.Builder::rightBottomRadius)
                pt("borderLeftBottomRadius", DynamicImage.Builder::leftBottomRadius)
                register("src", GlideModelFiller)
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): DynamicImage.Builder {
        return DynamicImage.create(c)
    }
}