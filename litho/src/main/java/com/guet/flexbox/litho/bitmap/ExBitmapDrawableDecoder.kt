package com.guet.flexbox.litho.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.guet.flexbox.litho.drawable.ExBitmapDrawable

class ExBitmapDrawableDecoder<DataType>(
        private val decoder: ResourceDecoder<DataType, Bitmap>,
        private val resources: Resources
) : ResourceDecoder<DataType, ExBitmapDrawable> {

    override fun handles(
            source: DataType,
            options: Options
    ): Boolean {
        return decoder.handles(source, options)
    }

    override fun decode(
            source: DataType,
            width: Int,
            height: Int,
            options: Options
    ): Resource<ExBitmapDrawable>? {
        val bitmapResource: Resource<Bitmap>? = decoder
                .decode(source, width, height, options)
        if (bitmapResource != null) {
            return LazyExBitmapDrawableResource(
                    bitmapResource,
                    resources,
                    options
            )
        }
        return null
    }
}