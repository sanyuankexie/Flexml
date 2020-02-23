package com.guet.flexbox.litho.load

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.guet.flexbox.litho.drawable.BitmapDrawable

class BitmapDrawableTranscoder : ResourceTranscoder<Bitmap, BitmapDrawable> {

    override fun transcode(
            toTranscode: Resource<Bitmap>,
            options: Options
    ): Resource<BitmapDrawable> {
        var scaleType = options.get(DrawableLoaderModule.scaleType)
        if (scaleType == null || scaleType == ImageView.ScaleType.MATRIX) {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        val cornerRadius = options
                .get(DrawableLoaderModule.cornerRadius)
                ?: CornerRadius.empty
        return BitmapDrawableResource(toTranscode, scaleType, cornerRadius)
    }

}