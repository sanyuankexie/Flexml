package com.guet.flexbox.litho.drawable.rounded

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.facebook.litho.drawable.ComparableDrawable
import com.guet.flexbox.litho.drawable.DrawableWrapper

class BorderDrawable(
        drawable: Drawable,
        @Px private val borderWidth: Float,
        @ColorInt private val borderColor: Int
) : DrawableWrapper<Drawable>(drawable),
        RoundedRadius by RoundedRadius.from(drawable),
        ComparableDrawable {

    private inner class MyDrawRoundedDelegate : DrawRoundedDelegate() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            paint.color = borderColor
            paint.strokeWidth = borderWidth
        }

        override fun draw(canvas: Canvas) {
            val path = buildPathIfDirty(this@BorderDrawable)
            canvas.drawPath(path, paint)
        }
    }

    private lateinit var drawDelegate: MyDrawRoundedDelegate

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        if (this::drawDelegate.isInitialized) {
            drawDelegate.onBoundChanged()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawDelegate.draw(canvas)
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        if (other is BorderDrawable) {
            if (borderColor == other.borderColor
                    && borderWidth == other.borderWidth) {
                val myDrawable = wrappedDrawable
                val otherDrawable = other.wrappedDrawable
                return if (myDrawable is ComparableDrawable
                        && otherDrawable is ComparableDrawable
                        && RoundedRadius.equals(this, other)) {
                    myDrawable.isEquivalentTo(otherDrawable)
                } else {
                    myDrawable == otherDrawable
                }
            }
        }
        return false
    }
}