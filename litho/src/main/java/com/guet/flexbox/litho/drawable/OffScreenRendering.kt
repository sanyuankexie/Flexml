package com.guet.flexbox.litho.drawable

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Matrix.ScaleToFit
import android.graphics.Path.Direction
import android.graphics.Shader.TileMode
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.ImageView.ScaleType
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class OffScreenRendering(
        scaleType: ScaleType,
        blurRadius: Float,
        blurSampling: Float,
        lt: Float,
        rt: Float,
        rb: Float,
        lb: Float
) : Transformation<Bitmap> {
    private val scaleType: ScaleType = kotlin.run {
        if (
                scaleType == ScaleType.MATRIX
        ) {
            ScaleType.FIT_XY
        } else {
            scaleType
        }
    }
    private val blurRadius: Float = min(25f, max(0f, blurRadius))
    private val blurSampling: Float = max(1f, blurSampling)
    private val radiusArray: FloatArray = kotlin.run {
        val array = floatArrayOf(
                lt,
                rt,
                rb,
                lb
        )
        if (
                array.size > 1 && array.size != 4
        ) {
            floatArrayOf(array[0])
        } else {
            if (array.sum() == 0f) {
                emptyArray
            } else {
                array
            }
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        val buffer = ByteBuffer.allocate((3 + radiusArray.size) * 4)
        buffer.putInt(scaleType.ordinal)
                .putFloat(blurRadius)
                .putFloat(blurSampling)
        radiusArray.forEach {
            buffer.putFloat(it)
        }
        messageDigest.update(buffer)
    }

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        val start = SystemClock.uptimeMillis()
        val needBlur = blurRadius > 0 && blurSampling >= 1
        if (scaleType == ScaleType.FIT_XY && !needBlur
                && radiusArray.sum() == 0f) {
            //fast path
            return resource
        }
        val pool = Glide.get(context).bitmapPool
        val inBitmap = resource.get()
        val inWidth = inBitmap.width
        val inHeight = inBitmap.height
        val content = if (needBlur) {
            val scaleCopy = getScaleCopyBitmap(
                    pool, inBitmap, blurSampling
            )
            blurBitmap(context, scaleCopy, blurRadius)
            scaleCopy
        } else {
            inBitmap
        }
        val safeConfig = getAlphaSafeConfig(inBitmap)
        val output = pool[outWidth, outHeight, safeConfig]
        output.setHasAlpha(true)
        performDraw(
                output,
                content,
                inWidth, inHeight,
                outWidth, outHeight,
                if (needBlur) {
                    blurSampling
                } else {
                    1f
                }
        )
        if (needBlur) {
            pool.put(content)
        }
        Log.i(LOG, "use time = ${SystemClock.uptimeMillis() - start}")
        return BitmapResource(output, pool)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) || (
                other is OffScreenRendering
                        && scaleType == other.scaleType
                        && blurRadius == other.blurRadius
                        && blurSampling == other.blurSampling
                        && radiusArray.contentEquals(other.radiusArray)
                )
    }

    private fun performDraw(
            output: Bitmap,
            content: Bitmap,
            inWidth: Int,
            inHeight: Int,
            outWidth: Int,
            outHeight: Int,
            zoom: Float
    ) {
        val canvas = Canvas(output)
        canvas.scale(zoom, zoom)
        if (scaleType == ScaleType.FIT_XY) {
            when {
                radiusArray.isEmpty() -> {
                    canvas.drawBitmap(
                            content,
                            null,
                            Rect(
                                    0,
                                    0,
                                    (outWidth / zoom).toInt(),
                                    (outHeight / zoom).toInt()
                            ),
                            null
                    )
                }
                radiusArray.size == 1 -> {
                    val paint = getBitmapPaint(content)
                    canvas.drawRoundRect(
                            RectF(
                                    0f,
                                    0f,
                                    outWidth / zoom,
                                    outHeight / zoom
                            ),
                            radiusArray[0] / zoom,
                            radiusArray[0] / zoom,
                            paint
                    )
                }
                else -> {
                    val paint = getBitmapPaint(content)
                    val path = buildPath(
                            outWidth,
                            outHeight,
                            zoom
                    )
                    canvas.drawPath(path, paint)
                }
            }
        } else {
            val matrix = createMatrix(
                    content,
                    scaleType,
                    outWidth,
                    outHeight
            )
            if (matrix != null) {
                canvas.concat(matrix)
            }
            when {
                radiusArray.isEmpty() -> {
                    canvas.drawBitmap(
                            content,
                            null,
                            Rect(
                                    0,
                                    0,
                                    (inWidth / zoom).toInt(),
                                    (inHeight / zoom).toInt()
                            ),
                            null
                    )
                }
                radiusArray.size == 1 -> {
                    val paint = getBitmapPaint(content)
                    canvas.drawRoundRect(
                            RectF(
                                    0f,
                                    0f,
                                    inWidth / zoom,
                                    inHeight / zoom
                            ),
                            radiusArray[0] / zoom,
                            radiusArray[0] / zoom,
                            paint
                    )
                }
                else -> {
                    val paint = getBitmapPaint(content)
                    val path = buildPath(
                            inWidth,
                            inHeight,
                            zoom
                    )
                    canvas.drawPath(path, paint)
                }
            }
        }
        canvas.setBitmap(null)
    }

    private fun buildPath(
            width: Int,
            height: Int,
            zoom: Float
    ): Path {
        return Path().apply {
            addRoundRect(
                    RectF(0f, 0f,
                            width / zoom,
                            height / zoom
                    ),
                    floatArrayOf(
                            radiusArray[0] / zoom,
                            radiusArray[0] / zoom,
                            radiusArray[1] / zoom,
                            radiusArray[1] / zoom,
                            radiusArray[2] / zoom,
                            radiusArray[2] / zoom,
                            radiusArray[3] / zoom,
                            radiusArray[3] / zoom
                    ),
                    Direction.CW
            )
        }
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + scaleType.hashCode()
        result = 31 * result + blurRadius.hashCode()
        result = 31 * result + blurSampling.hashCode()
        result = 31 * result + radiusArray.contentHashCode()
        return result
    }

    private companion object {

        private val LOG = "OffScreenRendering"

        private val emptyArray = floatArrayOf()

        private val ID = OffScreenRendering::class.java.name
        private val ID_BYTE = ID.toByteArray()

        private fun getBitmapPaint(content: Bitmap): Paint {
            val shader = BitmapShader(
                    content,
                    TileMode.CLAMP,
                    TileMode.CLAMP
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.shader = shader
            return paint
        }

        private fun getScaleCopyBitmap(
                pool: BitmapPool,
                bitmap: Bitmap,
                value: Float
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val scaledWidth = (width / value).toInt()
            val scaledHeight = (height / value).toInt()
            val safeConfig = getAlphaSafeConfig(bitmap)
            val output = pool[
                    scaledWidth,
                    scaledHeight,
                    safeConfig
            ]
            val canvas = Canvas(output)
            canvas.scale(1 / value, 1 / value)
            val paint = Paint()
            paint.flags = Paint.FILTER_BITMAP_FLAG
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            canvas.setBitmap(null)
            return output
        }

        private fun blurBitmap(
                context: Context,
                bitmap: Bitmap,
                blurRadius: Float
        ) {
            var rs: RenderScript? = null
            var input: Allocation? = null
            var output: Allocation? = null
            var blur: ScriptIntrinsicBlur? = null
            try {
                rs = RenderScript.create(context)
                input = Allocation.createFromBitmap(rs, bitmap,
                        Allocation.MipmapControl.MIPMAP_NONE,
                        Allocation.USAGE_SCRIPT)
                output = Allocation.createTyped(rs, input.type)
                blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

                blur.setInput(input)
                blur.setRadius(blurRadius)
                blur.forEach(output)
                output.copyTo(bitmap)
            } finally {
                rs?.destroy()
                input?.destroy()
                output?.destroy()
                blur?.destroy()
            }
        }

        private fun createMatrix(
                bitmap: Bitmap,
                scaleType: ScaleType,
                width: Int,
                height: Int
        ): Matrix? {
            val intrinsicWidth = bitmap.width
            val intrinsicHeight = bitmap.height
            if (intrinsicWidth <= 0 || intrinsicHeight <= 0
                    || ScaleType.FIT_XY == scaleType) {
                return null
            }
            if (width == intrinsicWidth && height == intrinsicHeight) {
                // The bitmap fits exactly, no transform needed.
                return null
            }
            val result = Matrix()
            //var shouldClipRect = false
            when (scaleType) {
                ScaleType.CENTER -> {
                    // Center bitmap in view, no scaling.
                    result.setTranslate(
                            round((width - intrinsicWidth) * 0.5f),
                            round((height - intrinsicHeight) * 0.5f)
                    )
                    //shouldClipRect = intrinsicWidth > width || intrinsicHeight > height
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
                    //shouldClipRect = true
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
            return result
        }

        private fun scaleTypeToScaleToFit(
                st: ScaleType
        ): ScaleToFit {
            // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
            return when (st) {
                ScaleType.FIT_XY -> ScaleToFit.FILL
                ScaleType.FIT_START -> ScaleToFit.START
                ScaleType.FIT_CENTER -> ScaleToFit.CENTER
                ScaleType.FIT_END -> ScaleToFit.END
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

    }
}