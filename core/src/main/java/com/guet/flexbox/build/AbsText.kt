package com.guet.flexbox.build

import android.text.TextUtils
import com.guet.flexbox.enums.TextStyle

internal object AbsText : Declaration() {

    override val dataBinding by DataBinding
            .create(CommonProps.dataBinding) {
                //TODO
//                enum("gravity", mapOf(
//                        "top" to Vertical.TOP,
//                        "bottom" to Vertical.BOTTOM,
//                        "center" to Vertical.CENTER
//                ))
//                @Suppress("NewApi")
//                enum("gravity", mapOf(
//                        "left" to Horizontal.LEFT,
//                        "right" to Horizontal.RIGHT,
//                        "center" to Horizontal.CENTER
//                ))
                enum("ellipsize", mapOf(
                        "start" to TextUtils.TruncateAt.START,
                        "end" to TextUtils.TruncateAt.END,
                        "middle" to TextUtils.TruncateAt.MIDDLE,
                        "marquee" to TextUtils.TruncateAt.MARQUEE
                ))
                value("maxLines", fallback = Int.MAX_VALUE.toFloat())
                value("minLines", fallback = Int.MIN_VALUE.toFloat())
                value("textSize", fallback = 13.0f)
                enum("textStyle", mapOf(
                        "normal" to TextStyle.NORMAL,
                        "bold" to TextStyle.BOLD
                ))
            }
}