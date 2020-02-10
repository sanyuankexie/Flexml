package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import java.security.MessageDigest

class RenderGroup(
        private val fitScale: FitScale,
        private val fastBlur: FastBlur? = null,
        private val corners: GranularRoundedCorners? = null
) : Transformation<Bitmap> {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        fitScale.updateDiskCacheKey(messageDigest)
        fastBlur?.updateDiskCacheKey(messageDigest)
        corners?.updateDiskCacheKey(messageDigest)
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) || (
                other is RenderGroup
                        && fitScale == other.fitScale
                        && fastBlur == other.fastBlur
                        && corners == other.corners
                )
    }

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        val startTime = SystemClock.uptimeMillis()
        try {
            val blurOutput = fastBlur?.transform(
                    context,
                    resource,
                    outWidth,
                    outHeight
            )
            val pool = Glide.get(context).bitmapPool
            val start = resource.get()
            val inWidth = start.width
            val inHeight = start.height
            val scaleOutput = if (blurOutput != null) {
                val bitmap = fitScale.safeTransform(
                        pool,
                        blurOutput.get(),
                        inWidth,
                        inHeight,
                        outWidth,
                        outHeight
                )
                if (blurOutput.get() != bitmap && blurOutput.get() != resource.get()) {
                    blurOutput.recycle()
                }
                BitmapResource(bitmap, pool)
            } else {
                val bitmap = fitScale.safeTransform(
                        pool,
                        start,
                        inWidth,
                        inHeight,
                        outWidth,
                        outHeight
                )
                BitmapResource(bitmap, pool)
            }
            return if (corners != null) {
                val cornersOutput = corners.transform(
                        context,
                        scaleOutput,
                        outWidth,
                        outHeight
                )
                if (scaleOutput.get() != cornersOutput.get()
                        && scaleOutput.get() != resource.get()) {
                    scaleOutput.recycle()
                }
                cornersOutput
            } else {
                scaleOutput
            }
        } finally {
            Log.i("OffScreenRendering", "use time: ${SystemClock.uptimeMillis() - startTime}")
        }
    }

    override fun hashCode(): Int {
        return arrayOf(
                fitScale,
                fastBlur,
                corners
        ).hashCode()
    }

    class Builder {
        var blurRadius: Float = 0f
        var blurSampling: Float = 0f
        var scaleType: ScaleType = ScaleType.FIT_XY
        var leftTop: Float = 0f
        var rightTop: Float = 0f
        var rightBottom: Float = 0f
        var leftBottom: Float = 0f

        fun build(): RenderGroup {
            val fitScale = FitScale(scaleType)
            var corners: GranularRoundedCorners? = null
            if (leftTop + rightTop + rightBottom + leftBottom != 0f) {
                corners = GranularRoundedCorners(
                        leftTop,
                        rightTop,
                        rightBottom,
                        leftBottom
                )
            }
            var fastBlur: FastBlur? = null
            if (blurRadius > 0 && blurSampling >= 1) {
                fastBlur = FastBlur(blurRadius, blurSampling)
            }
            return RenderGroup(fitScale, fastBlur, corners)
        }

        companion object {
            inline operator fun invoke(build: Builder.() -> Unit): Builder {
                val builder = Builder()
                builder.apply(build)
                return builder
            }
        }
    }
}