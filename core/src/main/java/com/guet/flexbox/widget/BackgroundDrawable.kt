package com.guet.flexbox.widget

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.facebook.litho.drawable.ComparableDrawable

internal class BackgroundDrawable(
        drawable: Drawable,
        radius: Int = 0,
        width: Int = 0,
        color: Int = Color.TRANSPARENT
) : DrawableWrapper<BorderDrawable<Drawable>>(BorderDrawable(drawable, radius, width, color))
        , ComparableDrawable {

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is BackgroundDrawable) {
            if (wrappedDrawable.borderWidth == other.wrappedDrawable.borderWidth
                    && wrappedDrawable.borderColor == other.wrappedDrawable.borderColor
                    && wrappedDrawable.radius == other.wrappedDrawable.radius) {
                return wrappedDrawable.wrappedDrawable ==
                        other.wrappedDrawable.wrappedDrawable
            }
        }
        return false
    }
}