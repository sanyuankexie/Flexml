package com.guet.flexbox.litho.drawable

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

class NoOpDrawable : Drawable() {

    init {
        super.setBounds(0, 0, 0, 0)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {}

    override fun setBounds(bounds: Rect) {}

    override fun draw(canvas: Canvas) {}

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}