package com.guet.flexbox.litho.transforms

import android.content.Context
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.Resource

interface TransformationEx<T> : Key {
    fun transform(
            context: Context,
            toTransform: Resource<T>,
            inWidth: Int,
            inHeight: Int,
            outWidth: Int,
            outHeight: Int
    ): Resource<T>
}