package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.facebook.litho.drawable.ComparableDrawable

class RoundedGlideDrawable(
        context: Context,
        model: Any,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : GlideDrawable(context, model), RoundedCorners {

    override fun buildRequest(
            builder: RequestBuilder<Drawable>
    ): RequestBuilder<Drawable> {
        val requestBuilder = super.buildRequest(builder)
        return if (hasRoundedCorners) {
            requestBuilder.transform(GranularRoundedCorners(
                    leftTop,
                    rightTop,
                    rightBottom,
                    leftBottom
            ))
        } else {
            requestBuilder
        }
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is RoundedGlideDrawable
                && super.isEquivalentTo(other)
                && RoundedCorners.equals(this, other)
    }
}