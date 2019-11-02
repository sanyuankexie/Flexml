package com.guet.flexbox.widget

import android.graphics.*
import android.graphics.drawable.Drawable

internal class BorderDrawable(
        drawable: Drawable = NoOpDrawable,
        radius: Int = 0,
        var width: Int = 0,
        var color: Int = Color.TRANSPARENT)
    : RoundedDrawable<Drawable>(drawable, radius) {

    private val path = Path()
    private val rectF = RectF()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (width > 0) {
            path.reset()
            path.addRoundRect(rectF.apply {
                set(
                        bounds.left - width / 2f,
                        bounds.top - width / 2f,
                        bounds.right + width / 2f,
                        bounds.bottom + width / 2f
                )
            }, radius.toFloat(), radius.toFloat(), Path.Direction.CW)
            path.close()
            paint.color = color
            paint.strokeWidth = width.toFloat()
            canvas.drawPath(path, paint)
        }
    }
}