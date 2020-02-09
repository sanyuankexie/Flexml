package com.guet.flexbox.litho.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.annotation.ColorInt
import com.facebook.litho.drawable.ComparableDrawable

open class LazyGradientDrawable(
        private val orientation: Orientation,
        @ColorInt private val gradientColors: IntArray
) : DrawableWrapper<Drawable>(NoOpDrawable()), ComparableDrawable {

    private val target by lazy {
        val value = getLazyDrawable()
        super.wrappedDrawable = value
        return@lazy value
    }

    protected open fun getLazyDrawable():GradientDrawable {
        return GradientDrawable(orientation, gradientColors)
    }

    override var wrappedDrawable: Drawable
        get() = target
        set(_) {}

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is LazyGradientDrawable
                && orientation == other.orientation
                && gradientColors.contentEquals(other.gradientColors)
    }
}