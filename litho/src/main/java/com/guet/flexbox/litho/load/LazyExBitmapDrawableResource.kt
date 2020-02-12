package com.guet.flexbox.litho.load

import android.content.res.Resources
import android.graphics.Bitmap
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Initializable
import com.bumptech.glide.load.engine.Resource
import com.guet.flexbox.litho.drawable.ExBitmapDrawable

class LazyExBitmapDrawableResource(
        private val bitmapResource: Resource<Bitmap>,
        private val resources: Resources,
        options: Options
) : Resource<ExBitmapDrawable>, Initializable {

    private val scaleType: ScaleType
    private val cornerRadius: FloatArray

    init {
        var scaleType = options.get(Constants.scaleType)
        if (scaleType == null || scaleType == ScaleType.MATRIX) {
            scaleType = ScaleType.FIT_XY
        }
        this.scaleType = scaleType
        cornerRadius = options.get(Constants.cornerRadii) ?: Constants.emptyArray
    }

    override fun getResourceClass(): Class<ExBitmapDrawable> {
        return ExBitmapDrawable::class.java
    }

    override fun get(): ExBitmapDrawable {
        val drawable = ExBitmapDrawable(bitmapResource.get())
        drawable.scaleType = scaleType
        if (cornerRadius.isNotEmpty()) {
            if (cornerRadius.size == 8) {
                drawable.cornerRadii = cornerRadius
            } else {
                drawable.cornerRadius = cornerRadius[0]
            }
        }
        return drawable
    }

    override fun getSize(): Int {
        return bitmapResource.size
    }

    override fun recycle() {
        bitmapResource.recycle()
    }

    override fun initialize() {
        if (bitmapResource is Initializable) {
            bitmapResource.initialize()
        }
    }
}