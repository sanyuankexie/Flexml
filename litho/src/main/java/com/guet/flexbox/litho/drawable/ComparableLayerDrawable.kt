package com.guet.flexbox.litho.drawable

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.facebook.litho.drawable.ComparableDrawable

class ComparableLayerDrawable(
        vararg layers: Drawable
) : LayerDrawable(layers), ComparableDrawable {
    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (this === other) {
            return true
        } else if (other is ComparableLayerDrawable
                && numberOfLayers == other.numberOfLayers) {
            return (0..numberOfLayers).all {
                val o1 = getDrawable(it)
                val o2 = other.getDrawable(it)
                if (o1 is ComparableDrawable && o2 is ComparableDrawable) {
                    o1.isEquivalentTo(o2)
                } else {
                    o1 == o2
                }
            }
        }
        return false
    }
}