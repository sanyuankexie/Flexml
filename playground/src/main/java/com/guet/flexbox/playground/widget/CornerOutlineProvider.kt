package com.guet.flexbox.playground.widget

import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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