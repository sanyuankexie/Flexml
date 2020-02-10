package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.math.MathUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.max

class FastBlur(
        private val radius: Float,
        private val sampling: Float
) : Transformation<Bitmap> {

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
        val bitmapPool = Glide.get(context).bitmapPool
        val toTransform = resource.get()
        val transformed = transform(context, bitmapPool, toTransform)
        return BitmapResource(transformed, bitmapPool)
    }

    private fun transform(
            context: Context,
            pool: BitmapPool,
            toTransform: Bitmap
    ): Bitmap {
        val sampling = max(this.sampling, 1f)
        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = (width / sampling).toInt()
        val scaledHeight = (height / sampling).toInt()
        val bitmap = pool[
                scaledWidth,
                scaledHeight,
                toTransform.config
        ]
        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling, 1 / sampling)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        return blur(context, bitmap)
    }

    override fun toString(): String {
        return "${FastBlur::class.java.name}(radius=$radius, sampling=$sampling)"
    }

    override fun equals(other: Any?): Boolean {
        return other is FastBlur
                && other.radius == radius
                && other.sampling == sampling
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        messageDigest.update(ByteBuffer.allocate(8)
                .putFloat(radius)
                .putFloat(sampling)
        )
    }

    private fun blur(
            context: Context,
            bitmap: Bitmap
    ): Bitmap {
        val radius = MathUtils.clamp(this.radius, 0f, 25f)
        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(context)
            rs.messageHandler = RenderScript.RSMessageHandler()
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
        return bitmap
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + sampling.hashCode()
        return result
    }

    private companion object {
        private val ID = FastBlur::class.java.name
        private val ID_BYTE = ID.toByteArray()
    }
}