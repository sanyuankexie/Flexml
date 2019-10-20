package com.guet.flexbox.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

internal object NoOpDrawable : Drawable() {

    override fun draw(canvas: Canvas) {
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun onBoundsChange(bounds: Rect?) {

    }

    override fun setBounds(bounds: Rect) {

    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
    }
}