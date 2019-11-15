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
            var inner = wrappedDrawable
            while (inner is DrawableWrapper<*>) {
                inner = inner.wrappedDrawable
            }
            var otherInner = other.wrappedDrawable
            while (otherInner is DrawableWrapper<*>) {
                otherInner = otherInner.wrappedDrawable
            }
            if (inner is ComparableDrawable && otherInner is ComparableDrawable) {
                return ComparableDrawable.isEquivalentTo(inner, otherInner)
            }
        }
        return false
    }

}