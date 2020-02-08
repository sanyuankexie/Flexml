package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.ScaleType
import com.guet.flexbox.litho.resolve.mapping
import com.guet.flexbox.litho.widget.GlideImage

internal object ToGlideImage : ToComponent<GlideImage.Builder>(CommonAssigns) {

    override val attributeAssignSet: AttributeAssignSet<GlideImage.Builder> by create {
        register("scaleType") { _, _, value: ScaleType ->
            scaleType(value.mapping())
        }
        register("blurRadius") { _, _, value: Double ->
            blurRadius(value.toFloat())
        }
        register("blurSampling") { _, _, value: Double ->
            blurSampling(value.toFloat())
        }
        register("aspectRatio") { _, _, value: Double ->
            imageAspectRatio(value.toFloat())
        }
        register("url") { _, _, value: Any ->
            if (value is Int) {
                resId(value)
            } else if (value is String) {
                url(value)
            }
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): GlideImage.Builder {
        return GlideImage.create(c)
    }
}