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
            fun getInnerDrawable(drawable: Drawable): ComparableDrawable {
                var innerDrawable: Drawable = drawable as DrawableWrapper<*>
                while (innerDrawable is DrawableWrapper<*>) {
                    innerDrawable = innerDrawable.wrappedDrawable
                }
                return innerDrawable as ComparableDrawable
            }
            return ComparableDrawable.isEquivalentTo(
                    getInnerDrawable(wrappedDrawable),
                    getInnerDrawable(other)
            )
        }
        return false
    }
}