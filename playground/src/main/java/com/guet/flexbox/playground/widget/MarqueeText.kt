package com.guet.flexbox.playground.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MarqueeText @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {
        isFocusable = true
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = Int.MIN_VALUE
    }

    override fun isFocused(): Boolean {
        return true
    }
}