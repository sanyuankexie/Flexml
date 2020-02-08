package com.guet.flexbox.litho.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.annotation.ColorInt
import com.facebook.litho.drawable.ComparableDrawable

class RoundedGradientDrawable(
        private val orientation: Orientation,
        @ColorInt private val gradientColors: IntArray,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : DrawableWrapper<Drawable>(NoOpDrawable()), RoundedCorners, ComparableDrawable {

    private val gradientDrawable by lazy {
        val value = GradientDrawable(orientation, gradientColors)
        value.cornerRadii = toRadiiArray(FloatArray(8))
        super.wrappedDrawable = value
        return@lazy value
    }

    override var wrappedDrawable: Drawable
        get() = gradientDrawable
        set(_) {}

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is RoundedGradientDrawable
                && orientation == other.orientation
                && gradientColors.contentEquals(other.gradientColors)
                && RoundedCorners.equals(this, other)
    }
}