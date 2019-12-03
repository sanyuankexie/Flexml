package com.luke.skywalker.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable

internal open class BorderDrawable<T : Drawable>(
        drawable: T,
        radius: Int = 0,
        var borderWidth: Int = 0,
        var borderColor: Int = Color.TRANSPARENT
) : RoundedDrawable<T>(drawable, radius) {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (borderWidth > 0) {
            paint.color = borderColor
            paint.strokeWidth = borderWidth.toFloat()
            canvas.drawPath(path, paint)
        }
    }
}