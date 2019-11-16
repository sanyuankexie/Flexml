package com.guet.flexbox.widget

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.litho.drawable.ComparableDrawableWrapper

internal class BackgroundDrawable(
        drawable: Drawable,
        radius: Int = 0,
        width: Int = 0,
        color: Int = Color.TRANSPARENT
) : ComparableDrawableWrapper(BorderDrawable(drawable, radius, width, color)) {

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is BackgroundDrawable) {
            val border = wrappedDrawable
            val otherBorder = other.wrappedDrawable
            if (border.borderWidth == otherBorder.borderWidth
                    && border.borderColor == otherBorder.borderColor
                    && border.radius == otherBorder.radius) {
                val left = unwrap()
                val right = other.unwrap()
                return if (left is ComparableDrawable && right is ComparableDrawable) {
                    ComparableDrawable.isEquivalentTo(left, right)
                } else {
                    left == right
                }
            }
        }
        return false
    }

    override fun getWrappedDrawable(): BorderDrawable<*> {
        return super.getWrappedDrawable() as BorderDrawable<*>
    }

    private fun unwrap(): Drawable {
        var innerDrawable: Drawable = wrappedDrawable
        while (innerDrawable is DrawableWrapper<*>) {
            innerDrawable = innerDrawable.wrappedDrawable
        }
        return innerDrawable
    }
}