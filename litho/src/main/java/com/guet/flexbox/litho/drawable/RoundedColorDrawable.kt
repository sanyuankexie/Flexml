package com.guet.flexbox.litho.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Path.Direction
import android.graphics.Path.FillType
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.facebook.litho.drawable.ComparableDrawable

class RoundedColorDrawable(
        @ColorInt private val baseColor: Int,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : Drawable(), RoundedCorners, ComparableDrawable {
    private var useColor: Int = baseColor

    private val drawKit by lazy { RoundedDrawKit() }

    override fun draw(canvas: Canvas) {
        drawKit.paint.apply {
            reset()
            isAntiAlias = true
            color = useColor
        }
        drawKit.path.apply {
            reset()
            if (hasRoundedCorners) {
                addRoundRect(
                        drawKit.rectF.apply {
                            set(bounds)
                        },
                        toRadiiArray(drawKit.array),
                        Direction.CW
                )
            } else {
                addRect(
                        drawKit.rectF.apply {
                            set(bounds)
                        },
                        Direction.CW
                )
            }
            close()
            fillType = FillType.WINDING
        }
        canvas.drawPath(drawKit.path, drawKit.paint)
    }

    override fun setAlpha(alpha: Int) {
        var a = alpha
        a += a shr 7 // make it 0..256
        val baseAlpha: Int = baseColor ushr 24
        val useAlpha = baseAlpha * a shr 8
        val useColor: Int = baseColor shl 8 ushr 8 or (useAlpha shl 24)
        if (useColor != useColor) {
            this.useColor = useColor
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int {
        return when (useColor ushr 24) {
            255 -> PixelFormat.OPAQUE
            0 -> PixelFormat.TRANSPARENT
            else -> PixelFormat.UNKNOWN
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is RoundedColorDrawable
                && other.baseColor == baseColor
                && RoundedCorners.equals(this, other)
    }
}