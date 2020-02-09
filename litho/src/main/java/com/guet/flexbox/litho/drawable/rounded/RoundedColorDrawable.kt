package com.guet.flexbox.litho.drawable.rounded

import android.graphics.*
import android.graphics.Path.FillType
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.facebook.litho.drawable.ComparableDrawable

class RoundedColorDrawable(
        @ColorInt private val baseColor: Int,
        override val leftTop: Float,
        override val rightTop: Float,
        override val rightBottom: Float,
        override val leftBottom: Float
) : Drawable(), RoundedRadius, ComparableDrawable {
    private var useColor: Int = baseColor

    private inner class MyDrawRoundedDelegate : DrawRoundedDelegate() {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun <T> buildPath(drawable: T, path: Path) where T : Drawable, T : RoundedRadius {
            super.buildPath(drawable, path)
            path.fillType = FillType.WINDING
        }

        override fun draw(canvas: Canvas) {
            paint.color = useColor
            val path = buildPathIfDirty(this@RoundedColorDrawable)
            canvas.drawPath(path, paint)
        }
    }

    private lateinit var drawDelegate: MyDrawRoundedDelegate

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        if (this::drawDelegate.isInitialized) {
            drawDelegate.onBoundChanged()
        }
    }

    override fun draw(canvas: Canvas) {
        if (!this::drawDelegate.isInitialized) {
            drawDelegate = MyDrawRoundedDelegate()
        }
        drawDelegate.draw(canvas)
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
                && RoundedRadius.equals(this, other)
    }
}