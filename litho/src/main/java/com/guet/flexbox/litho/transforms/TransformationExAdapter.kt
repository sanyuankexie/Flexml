package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import java.security.MessageDigest

internal class TransformationExAdapter<T : Transformation<Bitmap>>(
        internal val target: T
) : TransformationEx<Bitmap> {

    override fun transform(
            context: Context,
            toTransform: Resource<Bitmap>,
            inWidth: Int,
            inHeight: Int,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        return target.transform(
                context,
                toTransform,
                outWidth,
                outHeight
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        target.updateDiskCacheKey(messageDigest)
    }

    override fun hashCode(): Int {
        return target.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other === this)
                || (other is TransformationExAdapter<*> && target == other.target)
    }
}