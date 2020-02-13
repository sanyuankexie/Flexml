package com.guet.flexbox.playground.widget

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px
import kotlin.math.max

class CornerOutlineProvider(
        @Px private val borderRadius: Int
) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        if (borderRadius >= max(view.width, view.height)) {
            outline.setOval(
                    0,
                    0,
                    view.width,
                    view.height
            )
        } else {
            outline.setRoundRect(0,
                    0,
                    view.width,
                    view.height,
                    borderRadius.toFloat()
            )
        }
    }
}