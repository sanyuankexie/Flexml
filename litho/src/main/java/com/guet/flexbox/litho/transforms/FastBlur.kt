package com.guet.flexbox.litho.transforms

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.min

class FastBlur(
        radius: Float
) : Transformation<Bitmap> {

    private val radius: Float = min(25f, max(0f, radius))

    override fun transform(
            context: Context,
            resource: Resource<Bitmap>,
            outWidth: Int,
            outHeight: Int
    ): Resource<Bitmap> {
        if (radius <= 0f) {
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
        rsBlur(context, input)
        input.prepareToDraw()
        return resource
    }

    override fun toString(): String {
        return "${FastBlur::class.java.name}(radius=$radius)"
    }

    override fun equals(other: Any?): Boolean {
        return (this === other) || (other is FastBlur
                && other.radius == radius)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTE)
        messageDigest.update(ByteBuffer.allocate(4)
                .putFloat(radius)
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
        return result
    }


    private companion object {

        private val ID = FastBlur::class.java.name
        private val ID_BYTE = ID.toByteArray()
    }
}