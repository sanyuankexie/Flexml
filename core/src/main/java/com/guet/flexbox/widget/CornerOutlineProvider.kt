package com.guet.flexbox.widget

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px

class CornerOutlineProvider(@Px private val borderRadius: Int) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0, 0, view.width, view.height, borderRadius.toFloat())
    }
}