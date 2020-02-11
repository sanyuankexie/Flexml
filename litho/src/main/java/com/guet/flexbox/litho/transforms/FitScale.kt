package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Matrix.ScaleToFit
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.ImageView.ScaleType
import android.widget.ImageView.ScaleType.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.min
import kotlin.math.round

class FitScale(
        scaleType: ScaleType
) : Transformation<Bitmap>, TransformationEx<Bitmap> {

    private val scaleType: ScaleType = if (scaleType == MATRIX) {
        FIT_XY
    } else {
        scaleType
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        messageDigest.update(ByteBuffer.allocate(4)
                .putInt(scaleType.ordinal)
        )
    }

    override fun equals(other: Any?): Boolean {
        return (other === this) || (other is FitScale && scaleType == other.scaleType)
    }

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        return transform(
                context,
                resource,
                resource.get().width,
                resource.get().height,
                outWidth,
                outHeight
        )
    }

    override fun transform(
            context: Context,
            toTransform: Resource<Bitmap>,
            inWidth: Int,
            inHeight: Int,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        require(Util.isValidDimensions(outWidth, outHeight)) {
            ("Cannot apply transformation on width: "
                    + outWidth
                    + " or height: "
                    + outHeight
                    + " less than or equal to zero and not Target.SIZE_ORIGINAL")
        }
        val pool = Glide.get(context).bitmapPool
        val input = toTransform.get()
        if (scaleType == FIT_XY && input.width == outWidth
                && input.height == outHeight) {
            //fast path
            return toTransform
        }
        val safeInput = getAlphaSafeBitmap(pool, input)
        val output = transformInternal(
                pool,
                safeInput,
                inWidth,
                inHeight,
                outWidth,
                outHeight
        )
        if (safeInput != input) {
            pool.put(safeInput)
        }
        return BitmapResource(output, pool)
    }

    private fun transformInternal(
            pool: BitmapPool,
            input: Bitmap,
            inWidth: Int,
            inHeight: Int,
            outWidth: Int,
            outHeight: Int
    ): Bitmap {
        // Alpha is required for this transformation.
        val safeConfig = getAlphaSafeConfig(input)
        val output = pool[outWidth, outHeight, safeConfig]
        output.setHasAlpha(true)
        val canvas = Canvas(output)
        if (scaleType == FIT_XY) {
            canvas.drawBitmap(
                    input,
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
            val matrix = createMatrix(
                    inWidth,
                    inHeight,
                    scaleType,
                    outWidth,
                    outHeight
            )
            if (matrix != null) {
                canvas.concat(matrix)
            }
            canvas.drawBitmap(
                    input,
                    null,
                    Rect(
                            0,
                            0,
                            inWidth,
                            inHeight
                    ),
                    null
            )
        }
        canvas.setBitmap(null)
        return output
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + scaleType.hashCode()
        return result
    }

    private companion object {

        private val ID = FitScale::class.java.name
        private val ID_BYTE = ID.toByteArray()

        private fun createMatrix(
                intrinsicWidth: Int,
                intrinsicHeight: Int,
                scaleType: ScaleType,
                width: Int,
                height: Int
        ): Matrix? {
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

        private fun getAlphaSafeConfig(
                inBitmap: Bitmap
        ): Config {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                // Avoid short circuiting the sdk check.
                if (Config.RGBA_F16 == inBitmap.config) {
                    // NOPMD
                    return Config.RGBA_F16
                }
            }
            return Config.ARGB_8888
        }

        private fun getAlphaSafeBitmap(
                pool: BitmapPool,
                maybeAlphaSafe: Bitmap
        ): Bitmap {
            val safeConfig = getAlphaSafeConfig(maybeAlphaSafe)
            if (safeConfig == maybeAlphaSafe.config) {
                return maybeAlphaSafe
            }
            val argbBitmap = pool[
                    maybeAlphaSafe.width,
                    maybeAlphaSafe.height,
                    safeConfig]
            Canvas(argbBitmap).drawBitmap(
                    maybeAlphaSafe,
                    0f, 0f,
                    null /*paint*/
            )
            // We now own this Bitmap. It's our responsibility
            // to replace it in the pool outside this method
            // when we're finished with it.
            return argbBitmap
        }
    }
}