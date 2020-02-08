package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.min
import kotlin.math.round

class ImageScale(
        private val scaleType: ScaleType
) : Transformation<Bitmap> {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        messageDigest.update(ByteBuffer.allocate(4)
                .putInt(scaleType.ordinal)
        )
    }

    override fun equals(other: Any?): Boolean {
        return other is ImageScale && scaleType == other.scaleType
    }

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        if (scaleType == ScaleType.FIT_XY) {
            return resource
        }
        val bitmap = resource.get()
        val (matrix, shouldClipRect) = create(
                bitmap,
                scaleType,
                outWidth,
                outWidth
        )
        val pool = Glide.get(context).bitmapPool
        val output = pool[outWidth, outWidth, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(output)
        if (shouldClipRect) {
            canvas.clipRect(0, 0, outWidth, outWidth)
        }
        if (matrix != null) {
            canvas.concat(matrix)
        }
        canvas.drawBitmap(
                bitmap,
                null,
                Rect(
                        0,
                        0,
                        bitmap.width,
                        bitmap.height
                ),
                null
        )
        canvas.setBitmap(null)
        return BitmapResource(output, pool)
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + scaleType.hashCode()
        return result
    }

    private companion object {

        private val ID = ImageScale::class.java.name
        private val ID_BYTE = ID.toByteArray()

        private fun create(
                bitmap: Bitmap,
                scaleType: ScaleType,
                width: Int,
                height: Int
        ): Pair<Matrix?, Boolean> {
            val intrinsicWidth = bitmap.width
            val intrinsicHeight = bitmap.height
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0
                    || ScaleType.FIT_XY == scaleType
                    || ScaleType.MATRIX == scaleType) {
                return null to false
            }
            if (width == intrinsicWidth && height == intrinsicHeight) {
                // The bitmap fits exactly, no transform needed.
                return null to false
            }
            val result = Matrix()
            var shouldClipRect = false
            when (scaleType) {
                ScaleType.CENTER -> {
                    // Center bitmap in view, no scaling.
                    result.setTranslate(
                            round((width - intrinsicWidth) * 0.5f),
                            round((height - intrinsicHeight) * 0.5f)
                    )
                    shouldClipRect = intrinsicWidth > width || intrinsicHeight > height
                }
                ScaleType.CENTER_CROP -> {
                    val scale: Float
                    var dx = 0f
                    var dy = 0f
                    if (intrinsicWidth * height > width * intrinsicHeight) {
                        scale = height.toFloat() / intrinsicHeight.toFloat()
                        dx = (width - intrinsicWidth * scale) * 0.5f
                    } else {
                        scale = width.toFloat() / intrinsicWidth.toFloat()
                        dy = (height - intrinsicHeight * scale) * 0.5f
                    }
                    result.setScale(scale, scale)
                    result.postTranslate(round(dx), round(dy))
                    shouldClipRect = true
                }
                ScaleType.CENTER_INSIDE -> {
                    val scale: Float = if (intrinsicWidth <= width && intrinsicHeight <= height) {
                        1.0f
                    } else {
                        min(width.toFloat() / intrinsicWidth.toFloat(), height.toFloat() / intrinsicHeight.toFloat())
                    }
                    val dx = round((width - intrinsicWidth * scale) * 0.5f)
                    val dy = round((height - intrinsicHeight * scale) * 0.5f)
                    result.setScale(scale, scale)
                    result.postTranslate(dx, dy)
                }
                else -> {
                    val src = RectF()
                    val dest = RectF()
                    // Generate the required transform.
                    src[0f, 0f, intrinsicWidth.toFloat()] = intrinsicHeight.toFloat()
                    dest[0f, 0f, width.toFloat()] = height.toFloat()
                    result.setRectToRect(src, dest, scaleTypeToScaleToFit(scaleType))
                }
            }
            return result to shouldClipRect
        }

        private fun scaleTypeToScaleToFit(st: ScaleType): ScaleToFit {
            // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
            return when (st) {
                ScaleType.FIT_XY -> ScaleToFit.FILL
                ScaleType.FIT_START -> ScaleToFit.START
                ScaleType.FIT_CENTER -> ScaleToFit.CENTER
                ScaleType.FIT_END -> ScaleToFit.END
                else -> throw IllegalArgumentException("Only FIT_... values allowed")
            }
        }
    }

}