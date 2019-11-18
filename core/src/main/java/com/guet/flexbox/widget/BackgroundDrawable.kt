package com.guet.flexbox.widget

import android.graphics.Color
import com.facebook.litho.drawable.ComparableDrawable
import com.facebook.litho.drawable.ComparableDrawableWrapper

internal class BackgroundDrawable(
        drawable: ComparableDrawable,
        radius: Int = 0,
        width: Int = 0,
        color: Int = Color.TRANSPARENT
) : ComparableDrawableWrapper(BorderDrawable(drawable, radius, width, color)) {

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other == this) {
            return true
        }
        if (other is BackgroundDrawable) {
            if (border.borderWidth == other.border.borderWidth
                    && border.borderColor == other.border.borderColor
                    && border.radius == other.border.radius) {
                return ComparableDrawable.isEquivalentTo(
                        border.wrappedDrawable,
                        other.border.wrappedDrawable
                )
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    private val border: BorderDrawable<ComparableDrawable>
        get() = wrappedDrawable as BorderDrawable<ComparableDrawable>
}