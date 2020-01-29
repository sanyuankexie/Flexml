package com.guet.flexbox.litho.factories

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.ScaleType
import com.guet.flexbox.litho.widget.NetworkImage

internal object ToImage : ToComponent<NetworkImage.Builder>(Common) {

    override val attributeAssignSet: AttributeAssignSet<NetworkImage.Builder> by create {
        register("scaleType") { _, _, value: ScaleType ->
            scaleType(value.mapToLithoValue())
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
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): NetworkImage.Builder {
        val image = NetworkImage.create(c)
        val url = attrs["url"]
        val ctx = c.androidContext
        if (visibility && url is CharSequence) {
            when (val model = parseUrl(ctx, url)) {
                is CharSequence -> {
                    Glide.with(ctx)
                            .load(model)
                            .preload()
                    image.url(model)
                }
                is Int -> {
                    Glide.with(ctx)
                            .load(model)
                            .preload()
                    image.resId(model)
                }
                is Drawable -> {
                    image.drawable(model)
                }
                else -> {
                    image.url("")
                }
            }
        } else {
            image.url("")
        }
        return image
    }
}