package com.guet.flexbox.litho.drawable.rounded

import android.graphics.drawable.GradientDrawable.Orientation
import androidx.annotation.ColorInt
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.litho.drawable.GradientDrawable
import com.guet.flexbox.litho.drawable.LazyGradientDrawable

class RoundedGradientDrawable(
        orientation: Orientation,
        @ColorInt gradientColors: IntArray,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : LazyGradientDrawable(orientation, gradientColors)
        , RoundedRadius {

    override fun getLazyDrawable(): GradientDrawable {
        return super.getLazyDrawable().apply {
            cornerRadii = toRadiiArray(FloatArray(8))
        }
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is RoundedGradientDrawable
                && super.isEquivalentTo(other)
                && RoundedRadius.equals(this, other)
    }
}