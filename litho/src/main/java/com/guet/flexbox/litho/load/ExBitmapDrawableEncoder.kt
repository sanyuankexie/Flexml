package com.guet.flexbox.litho.load

import android.graphics.Bitmap
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceEncoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.guet.flexbox.litho.drawable.ExBitmapDrawable
import java.io.File

class ExBitmapDrawableEncoder(
        private val bitmapPool: BitmapPool,
        private val encoder: ResourceEncoder<Bitmap>
) : ResourceEncoder<ExBitmapDrawable> {

    override fun encode(
            data: Resource<ExBitmapDrawable>, file: File, options: Options): Boolean {
        return encoder.encode(BitmapResource(
                requireNotNull(data.get().bitmap),
                bitmapPool),
                file,
                options
        )
    }

    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        return encoder.getEncodeStrategy(options)
    }
}