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
            fun unwrap(drawable: BorderDrawable<*>): ComparableDrawable {
                var innerDrawable: Drawable = drawable
                while (innerDrawable is DrawableWrapper<*>) {
                    innerDrawable = innerDrawable.wrappedDrawable
                }
                return innerDrawable as ComparableDrawable
            }

            val border = wrappedDrawable
            val otherBorder = other.wrappedDrawable
            if (border.borderWidth == otherBorder.borderWidth
                    && border.borderColor == otherBorder.borderColor
                    && border.radius == otherBorder.radius) {
                return ComparableDrawable.isEquivalentTo(
                        unwrap(border),
                        unwrap(otherBorder)
                )
            }
        }
        return false
    }

    override fun getWrappedDrawable(): BorderDrawable<*> {
        return super.getWrappedDrawable() as BorderDrawable<*>
    }

}