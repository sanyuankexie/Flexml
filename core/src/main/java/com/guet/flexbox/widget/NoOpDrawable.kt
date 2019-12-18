package com.guet.flexbox.widget

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import com.facebook.litho.drawable.ComparableDrawable

internal class NoOpDrawable : ComparableDrawable() {

    init {
        super.setBounds(0, 0, 0, 0)
    }

    override fun isEquivalentTo(other: ComparableDrawable?): Boolean {
        return other is NoOpDrawable
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {}

    override fun setBounds(bounds: Rect) {}

    override fun draw(canvas: Canvas) {}

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}
}