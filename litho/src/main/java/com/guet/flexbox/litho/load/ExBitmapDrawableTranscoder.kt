package com.guet.flexbox.litho.load

import android.content.res.Resources
import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.guet.flexbox.litho.drawable.ExBitmapDrawable

class ExBitmapDrawableTranscoder(
        private val resources: Resources
) : ResourceTranscoder<Bitmap, ExBitmapDrawable> {
    override fun transcode(
            toTranscode: Resource<Bitmap>, options: Options): Resource<ExBitmapDrawable>? {
        return LazyExBitmapDrawableResource(resources, toTranscode)
    }

}