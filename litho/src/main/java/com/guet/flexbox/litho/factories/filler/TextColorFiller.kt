package com.guet.flexbox.litho.factories.filler

import android.content.res.ColorStateList
import android.graphics.Color
import com.facebook.litho.widget.Text

internal object TextColorFiller : PropFiller<Text.Builder, Int> {

    private val invisibleColor = ColorStateList.valueOf(Color.TRANSPARENT)

    override fun fill(
            c: Text.Builder,
            display: Boolean,
            other: Map<String, Any>,
            value: Int
    ) {
        if (display) {
            c.textColor(value)
        } else {
            c.textColor(Color.TRANSPARENT)
            c.textColorStateList(invisibleColor)
        }
    }
}