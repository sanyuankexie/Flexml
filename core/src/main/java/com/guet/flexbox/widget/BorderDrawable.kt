package com.guet.flexbox.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable

internal open class BorderDrawable<T : Drawable>(
        drawable: T,
        radius: Int = 0,
        var width: Int = 0,
        var color: Int = Color.TRANSPARENT
) : RoundedDrawable<T>(drawable, radius) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (width > 0) {
            paint.color = color
            paint.strokeWidth = width.toFloat()
            canvas.drawPath(path, paint)
        }
    }
}