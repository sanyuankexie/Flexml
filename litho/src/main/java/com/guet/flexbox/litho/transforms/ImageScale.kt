package com.guet.flexbox.litho.transforms

import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.widget.ImageView.ScaleType
import android.widget.ImageView.ScaleType.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.min
import kotlin.math.round

class ImageScale(
        private val scaleType: ScaleType
) : BitmapTransformation() {

    init {
        if (scaleType == MATRIX) {
            throw IllegalArgumentException()
        }
    }

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
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
    ): Bitmap {
        if (scaleType == FIT_XY
                && toTransform.width == outWidth
                && toTransform.height == outHeight) {
            return toTransform
        }
        // Alpha is required for this transformation.
        val safeConfig = getAlphaSafeConfig(toTransform)
        val safeToTransform = getAlphaSafeBitmap(pool, toTransform)
        val output = pool[outWidth, outHeight, safeConfig]
        output.setHasAlpha(true)
        val canvas = Canvas(output)
        if (scaleType == FIT_XY) {
            canvas.drawBitmap(
                    safeToTransform,
                    null,
                    Rect(
                            0,
                            0,
                            outWidth,
                            outHeight
                    ),
                    null
            )
        } else {
            val matrix = create(
                    toTransform,
                    scaleType,
                    outWidth,
                    outHeight
            )
            if (matrix != null) {
                canvas.concat(matrix)
            }
            canvas.drawBitmap(
                    safeToTransform,
                    null,
                    Rect(
                            0,
                            0,
                            safeToTransform.width,
                            safeToTransform.height
                    ),
                    null
            )
        }
        canvas.setBitmap(null)
        if (safeToTransform != toTransform) {
            pool.put(safeToTransform)
        }
        return output
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
        ): Matrix? {
            val intrinsicWidth = bitmap.width
            val intrinsicHeight = bitmap.height
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0
                    || FIT_XY == scaleType) {
                return null
            }
            if (width == intrinsicWidth && height == intrinsicHeight) {
                // The bitmap fits exactly, no transform needed.
                return null
            }
            val result = Matrix()
            //var shouldClipRect = false
            when (scaleType) {
                CENTER -> {
                    // Center bitmap in view, no scaling.
                    result.setTranslate(
                            round((width - intrinsicWidth) * 0.5f),
                            round((height - intrinsicHeight) * 0.5f)
                    )
                    //shouldClipRect = intrinsicWidth > width || intrinsicHeight > height
                }
                CENTER_CROP -> {
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
                    //shouldClipRect = true
                }
                CENTER_INSIDE -> {
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
            return result
        }

        private fun scaleTypeToScaleToFit(st: ScaleType): ScaleToFit {
            // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
            return when (st) {
                FIT_XY -> ScaleToFit.FILL
                FIT_START -> ScaleToFit.START
                FIT_CENTER -> ScaleToFit.CENTER
                FIT_END -> ScaleToFit.END
                else -> throw IllegalArgumentException("Only FIT_... values allowed")
            }
        }
    }

}