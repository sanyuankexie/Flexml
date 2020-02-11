package com.guet.flexbox.litho.drawable

import android.graphics.*
import android.graphics.Path.Direction
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px

class ColorBorderDrawable(
        @Px private val borderWidth: Float,
        @ColorInt private val borderColor: Int,
        @Px leftTop: Float = 0f,
        @Px rightTop: Float = 0f,
        @Px rightBottom: Float = 0f,
        @Px leftBottom: Float = 0f
) : Drawable() {

    companion object {
        private val emptyArray = FloatArray(0)
    }

    private val array: FloatArray

    init {
        array = if (leftTop + rightTop + rightBottom + leftBottom == 0f) {
            emptyArray
        } else if (leftTop == rightTop
                && leftTop == rightBottom
                && leftTop == leftBottom) {
            floatArrayOf(leftTop)
        } else {
            floatArrayOf(
                    leftTop, leftTop,
                    rightTop, rightTop,
                    rightBottom, rightBottom,
                    leftBottom, leftBottom
            )
        }
    }

    private val equalsRadius: Boolean
        get() = array.size == 1
    private val hasRadius: Boolean
        get() = array.isNotEmpty()
    private var isPathDirty: Boolean = true
    @ColorInt
    private var useColor = borderColor
    private lateinit var rectF: RectF
    private lateinit var paint: Paint
    private lateinit var path: Path

    private fun buildPathIfDirty() {
        if (isPathDirty) {
            if (!this::rectF.isInitialized) {
                rectF = RectF()
            }
            if (!this::path.isInitialized) {
                path = Path()
            }
            path.reset()
            if (hasRadius) {
                if (equalsRadius) {
                    path.addRoundRect(
                            rectF.apply {
                                set(bounds)
                            },
                            array[0],
                            array[0],
                            Direction.CW
                    )
                } else {
                    path.addRoundRect(
                            rectF.apply {
                                set(bounds)
                            },
                            array,
                            Direction.CW
                    )
                }
            } else {
                path.addRect(
                        rectF.apply {
                            set(bounds)
                        },
                        Direction.CW
                )
            }
            isPathDirty = false
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        isPathDirty = true
    }

    override fun draw(canvas: Canvas) {
        ensurePaint()
        buildPathIfDirty()
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(a: Int) {
        isPathDirty = true
        var alpha = a
        // make it 0..256
        alpha += alpha shr 7
        val baseAlpha: Int = borderColor ushr 24
        val useAlpha = baseAlpha * alpha shr 8
        val useColor: Int = borderColor shl 8 ushr 8 or (useAlpha shl 24)
        if (this.useColor != useColor) {
            this.useColor = useColor
            invalidateSelf()
        }
    }

    override fun getOpacity(): Int {
        when (useColor ushr 24) {
            255 -> return PixelFormat.OPAQUE
            0 -> return PixelFormat.TRANSPARENT
        }
        return PixelFormat.TRANSLUCENT
    }

    private fun ensurePaint() {
        if (!this::paint.isInitialized) {
            paint = Paint()
            paint.strokeWidth = borderWidth
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        ensurePaint()
        paint.colorFilter = colorFilter
    }

}