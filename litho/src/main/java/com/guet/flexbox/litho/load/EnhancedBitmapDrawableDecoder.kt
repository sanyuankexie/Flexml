package com.guet.flexbox.litho.load

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.guet.flexbox.litho.drawable.EnhancedBitmapDrawable

class EnhancedBitmapDrawableDecoder : ResourceDecoder<Bitmap, EnhancedBitmapDrawable> {
    override fun handles(source: Bitmap, options: Options): Boolean {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decode(source: Bitmap, width: Int, height: Int, options: Options): Resource<EnhancedBitmapDrawable>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}