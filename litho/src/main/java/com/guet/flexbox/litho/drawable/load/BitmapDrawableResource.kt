package com.guet.flexbox.litho.drawable.load

import android.graphics.Bitmap
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.engine.Initializable
import com.bumptech.glide.load.engine.Resource
import com.guet.flexbox.litho.drawable.BitmapDrawable

class BitmapDrawableResource(
        private val bitmapResource: Resource<Bitmap>,
        private val scaleType: ScaleType,
        private val cornerRadius: CornerRadius
) : Resource<BitmapDrawable> {

    override fun getResourceClass(): Class<BitmapDrawable> {
        return BitmapDrawable::class.java
    }

    override fun get(): BitmapDrawable {
        val drawable = BitmapDrawable(bitmapResource.get())
        drawable.scaleType = scaleType
        if (cornerRadius.hasRadius) {
            if (cornerRadius.hasEqualRadius) {
                drawable.cornerRadius = cornerRadius.radius
            } else {
                drawable.cornerRadii = cornerRadius.radii
            }
        }
        return drawable
    }

    override fun getSize(): Int {
        return bitmapResource.size
    }

    override fun recycle() {
        if (bitmapResource is Initializable) {
            bitmapResource.initialize()
        }
    }
}