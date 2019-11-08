package com.guet.flexbox.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.max

internal class BlurTransformation(
        private val context: Context,
        private val radius: Float,
        private val sampling: Float
) : BitmapTransformation() {

    override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int): Bitmap {
        val sampling = max(this.sampling, 1f)
        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = (width / sampling).toInt()
        val scaledHeight = (height / sampling).toInt()
        val bitmap = pool[
                scaledWidth,
                scaledHeight,
                Bitmap.Config.ARGB_8888
        ]
        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling, 1 / sampling)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        return blur(bitmap, max(radius, 25f))
    }

    override fun toString(): String {
        return "${BlurTransformation::class.java.name}(radius=$radius, sampling=$sampling)"
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation &&
                other.radius == radius &&
                other.sampling == sampling
    }

    override fun hashCode(): Int {
        return (ID.hashCode() + radius * 1000 + sampling * 10).toInt()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + sampling).toByteArray())
    }

    private fun blur(
            bitmap: Bitmap,
            radius: Float
    ): Bitmap {
        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(context)
            rs.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT)
            output = Allocation.createTyped(rs, input.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            blur.setInput(input)
            blur.setRadius(radius)
            blur.forEach(output)
            output.copyTo(bitmap)
        } finally {
            if (rs != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RenderScript.releaseAllContexts()
                } else {
                    rs.destroy()
                }
            }
            input?.destroy()
            output?.destroy()
            blur?.destroy()
        }
        return bitmap
    }

    internal companion object {
        internal val ID = BlurTransformation::class.java.name
    }
}