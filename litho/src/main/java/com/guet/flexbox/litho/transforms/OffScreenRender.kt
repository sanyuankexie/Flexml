package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import java.security.MessageDigest

class OffScreenRender(
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
                other is OffScreenRender
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
        val start = SystemClock.uptimeMillis()
        try {
            val blurOutput = fastBlur?.transform(
                    context,
                    resource,
                    outWidth,
                    outHeight
            )
            val inWidth = resource.get().width
            val inHeight = resource.get().height
            val scaleOutput = if (blurOutput != null) {
                val currentOutput = fitScale.transform(
                        context,
                        blurOutput,
                        inWidth,
                        inHeight,
                        outWidth,
                        outHeight
                )
                if (blurOutput != resource
                        && blurOutput != currentOutput) {
                    blurOutput.recycle()
                }
                currentOutput
            } else {
                fitScale.transform(
                        context,
                        resource,
                        inWidth,
                        inHeight,
                        outWidth,
                        outHeight
                )
            }
            return if (corners != null) {
                val cornersOutput = corners.transform(
                        context,
                        scaleOutput,
                        outWidth,
                        outHeight
                )
                if (scaleOutput != cornersOutput
                        && scaleOutput != resource) {
                    scaleOutput.recycle()
                }
                cornersOutput
            } else {
                scaleOutput
            }
        } finally {
            Log.i(LOG, "use time: ${SystemClock.uptimeMillis() - start}")
        }
    }

    override fun hashCode(): Int {
        return arrayOf(
                fitScale,
                fastBlur,
                corners
        ).hashCode()
    }

    private companion object{
        private val LOG = OffScreenRender::class.java.simpleName
    }

    class Builder {
        var blurRadius: Float = 0f
        var blurSampling: Float = 0f
        var scaleType: ScaleType = ScaleType.FIT_XY
        var leftTop: Float = 0f
        var rightTop: Float = 0f
        var rightBottom: Float = 0f
        var leftBottom: Float = 0f

        fun build(): OffScreenRender {
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
            return OffScreenRender(fitScale, fastBlur, corners)
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