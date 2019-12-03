package com.luke.skywalker.widget

import android.graphics.Color
import com.facebook.litho.drawable.ComparableDrawable

internal class BackgroundDrawable(
        drawable: ComparableDrawable,
        radius: Int = 0,
        width: Int = 0,
        color: Int = Color.TRANSPARENT
) : ComparableDrawableWrapper<BorderDrawable<ComparableDrawable>>(BorderDrawable(drawable, radius, width, color)) {

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is BackgroundDrawable) {
            if (wrappedDrawable.borderWidth == other.wrappedDrawable.borderWidth
                    && wrappedDrawable.borderColor == other.wrappedDrawable.borderColor
                    && wrappedDrawable.radius == other.wrappedDrawable.radius) {
                return isEquivalentTo(
                        wrappedDrawable.wrappedDrawable,
                        other.wrappedDrawable.wrappedDrawable
                )
            }
        }
        return false
    }
}