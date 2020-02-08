package com.guet.flexbox.litho.drawable

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.facebook.litho.drawable.ComparableDrawable

class BorderDrawable(
        drawable: Drawable,
        @Px private val borderWidth: Float,
        @ColorInt private val borderColor: Int
) : DrawableWrapper<Drawable>(drawable),
        RoundedCorners by RoundedCorners.from(drawable),
        ComparableDrawable {

    private val drawKit by lazy { RoundedDrawKit() }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawKit.paint.apply {
            reset()
            isAntiAlias = true
            color = borderColor
            strokeWidth = borderWidth
        }
        drawKit.path.apply {
            reset()
            if (hasRoundedCorners) {
                addRoundRect(
                        drawKit.rectF.apply {
                            set(bounds)
                        },
                        toRadiiArray(drawKit.array),
                        Path.Direction.CW
                )
            } else {
                addRect(
                        drawKit.rectF.apply {
                            set(bounds)
                        },
                        Path.Direction.CW
                )
            }
            close()
        }
        canvas.drawPath(drawKit.path, drawKit.paint)
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other is BorderDrawable) {
            if (borderColor == other.borderColor
                    && borderWidth == other.borderWidth) {
                val myDrawable = wrappedDrawable
                val otherDrawable = other.wrappedDrawable
                return if (myDrawable is ComparableDrawable
                        && otherDrawable is ComparableDrawable
                        && RoundedCorners.equals(this, other)) {
                    myDrawable.isEquivalentTo(otherDrawable)
                } else {
                    myDrawable == otherDrawable
                }
            }
        }
        return false
    }
}