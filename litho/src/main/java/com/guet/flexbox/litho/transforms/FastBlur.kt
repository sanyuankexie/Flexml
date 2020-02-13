package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.util.Util
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

class FastBlur(
        radius: Float,
        sampling: Float
) : Transformation<Bitmap> {

    private val radius: Float = min(25f, max(0f, radius))
    private val sampling: Float = max(1f, sampling)

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        if (radius <= 0f || sampling < 1f) {
            return resource
        }
        require(Util.isValidDimensions(outWidth, outHeight)) {
            ("Cannot apply transformation on width: "
                    + outWidth
                    + " or height: "
                    + outHeight
                    + " less than or equal to zero and not Target.SIZE_ORIGINAL")
        }
        val input = resource.get()
        val sampling = max(this.sampling, 1f)
        val width = input.width
        val height = input.height
        if (sampling != 1f) {
            val scaledWidth = (width / sampling).toInt()
            val scaledHeight = (height / sampling).toInt()
            val buffer = Buffer()
            val paint = Paint(DEFAULT_PAINT_FLAGS)
            val canvas = buffer.beginRecording(scaledWidth, scaledHeight)
            canvas.drawBitmap(
                    input,
                    Rect(0, 0, input.width, input.height),
                    Rect(0, 0, scaledWidth, scaledHeight),
                    paint
            )
            buffer.endRecording()
            input.reconfigure(scaledWidth, scaledHeight, input.config)
            val canvas2 = Canvas(input)
            canvas2.drawPicture(buffer)
            canvas2.setBitmap(null)
            canvas2.release()
            canvas.release()
            buffer.finalize()
        }
        rsBlur(context, input)
        return resource
    }


    override fun toString(): String {
        return "${FastBlur::class.java.name}(radius=$radius, sampling=$sampling)"
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) || (other is FastBlur
                && other.radius == radius
                && other.sampling == sampling)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        messageDigest.update(ByteBuffer.allocate(8)
                .putFloat(radius)
                .putFloat(sampling)
        )
    }

    private fun rsBlur(
            context: Context,
            bitmap: Bitmap
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
            blur.setRadius(radius)
            blur.forEach(output)
            output.copyTo(bitmap)
        } finally {
            rs?.destroy()
            input?.destroy()
            output?.destroy()
            blur?.destroy()
        }
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + sampling.hashCode()
        return result
    }

    private class Buffer : Picture() {

        private val isFinalize = AtomicBoolean(false)

        public override fun finalize() {
            if (isFinalize.compareAndSet(false, true)) {
                super.finalize()
            }
        }
    }

    private companion object {

        private const val DEFAULT_PAINT_FLAGS = Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG

        private val release by buildMethod<Canvas>("release")

        private inline fun <reified T> buildMethod(name: String): Lazy<T.() -> Unit> {
            return lazy<T.() -> Unit> {
                var method: Method? = null
                try {
                    method = T::class.java
                            .getDeclaredMethod(name)
                            .apply {
                                isAccessible = true
                            }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                return@lazy {
                    try {
                        method?.invoke(this)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }

        private val ID = FastBlur::class.java.name
        private val ID_BYTE = ID.toByteArray()
    }
}