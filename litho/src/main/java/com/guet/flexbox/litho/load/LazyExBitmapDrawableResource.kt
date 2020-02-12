package com.guet.flexbox.litho.load

import android.content.res.Resources
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.Initializable
import com.bumptech.glide.load.engine.Resource
import com.guet.flexbox.litho.drawable.ExBitmapDrawable

class LazyExBitmapDrawableResource(
        private val resources: Resources,
        private val bitmapResource: Resource<Bitmap>
) : Resource<ExBitmapDrawable>, Initializable {

    override fun getResourceClass(): Class<ExBitmapDrawable> {
        return ExBitmapDrawable::class.java
    }

    override fun get(): ExBitmapDrawable {
        return ExBitmapDrawable(bitmapResource.get())
    }

    override fun getSize(): Int {
        return bitmapResource.size
    }

    override fun recycle() {
        bitmapResource.recycle()
    }

    override fun initialize() {
        if (bitmapResource is Initializable){
            bitmapResource.initialize()
        }
    }
}