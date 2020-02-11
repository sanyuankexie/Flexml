package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.security.MessageDigest

class OffScreenRender internal constructor(
        private val queue: Array<TransformationEx<Bitmap>>
) : Transformation<Bitmap> {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        queue.forEach {
            it.updateDiskCacheKey(messageDigest)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) || (
                other is OffScreenRender && queue.contentEquals(other.queue)
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
            val inWidth = resource.get().width
            val inHeight = resource.get().height
            var previous: Resource<Bitmap> = resource
            for (transformation in queue) {
                val transformed: Resource<Bitmap> = transformation
                        .transform(
                                context, previous,
                                inWidth, inHeight,
                                outWidth, outHeight
                        )
                if (previous != resource && previous != transformed) {
                    previous.recycle()
                }
                previous = transformed
            }
            return previous
        } finally {
            Log.i(LOG, "use time: ${SystemClock.uptimeMillis() - start}")
        }
    }

    override fun hashCode(): Int {
        return queue.hashCode()
    }

    private companion object {
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

        fun build(): OffScreenRender? {
            var corners: Transformation<Bitmap>? = null
            if (leftTop + rightTop + rightBottom + leftBottom != 0f) {
                corners = if (leftTop == rightTop
                        && leftTop == rightBottom
                        && leftTop == leftBottom) {
                    RoundedCorners(
                            leftTop.toInt()
                    )
                } else {
                    GranularRoundedCorners(
                            leftTop,
                            rightTop,
                            rightBottom,
                            leftBottom
                    )
                }
            }
            var fastBlur: FastBlur? = null
            if (blurRadius > 0 && blurSampling >= 1) {
                fastBlur = FastBlur(blurRadius, blurSampling)
            }
            var fitScale: FitScale? = null
            if (!(scaleType == ScaleType.FIT_XY
                            && corners == null
                            && fastBlur == null)) {
                fitScale = FitScale(scaleType)
            }
            val list = ArrayList<TransformationEx<Bitmap>>()
            if (fastBlur != null) {
                list.add(TransformationExAdapter(fastBlur))
            }
            if (fitScale != null) {
                list.add(fitScale)
            }
            if (corners != null) {
                list.add(TransformationExAdapter(corners))
            }
            return if (list.isEmpty()) {
                null
            } else {
                OffScreenRender(list.toTypedArray())
            }
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