package com.guet.flexbox.widget

import android.graphics.*
import android.graphics.drawable.Drawable

internal open class BorderDrawable<T : Drawable>(
        drawable: T,
        var radius: Float = 0f,
        var width: Float = 0f,
        var color: Int = Color.TRANSPARENT)
    : DrawableWrapper<T>(drawable) {

    private val path = Path()
    private val rectF = RectF()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        path.reset()
        path.addRoundRect(rectF.apply {
            set(bounds)
        }, radius, radius, Path.Direction.CW)
        path.close()
        if (radius > 0) {
            val sc = canvas.save()
            canvas.clipPath(path)
            super.draw(canvas)
            canvas.restoreToCount(sc)
        } else {
            super.draw(canvas)
        }
        if (width > 0) {
            paint.color = color
            paint.strokeWidth = width
            canvas.drawPath(path, paint)
        }
    }
}