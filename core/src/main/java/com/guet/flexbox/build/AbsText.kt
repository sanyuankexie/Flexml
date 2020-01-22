package com.guet.flexbox.build

import android.text.TextUtils
import com.guet.flexbox.HorizontalGravity
import com.guet.flexbox.TextStyle
import com.guet.flexbox.VerticalGravity

internal object AbsText : Declaration(Common) {

    override val attributeInfoSet: AttributeInfoSet by create {
        enum("verticalGravity", mapOf(
                "top" to VerticalGravity.TOP,
                "bottom" to VerticalGravity.BOTTOM,
                "center" to VerticalGravity.CENTER
        ))
        @Suppress("NewApi")
        enum("horizontalGravity", mapOf(
                "left" to HorizontalGravity.LEFT,
                "right" to HorizontalGravity.RIGHT,
                "center" to HorizontalGravity.CENTER
        ))
        enum("ellipsize", mapOf(
                "start" to TextUtils.TruncateAt.START,
                "end" to TextUtils.TruncateAt.END,
                "middle" to TextUtils.TruncateAt.MIDDLE,
                "marquee" to TextUtils.TruncateAt.MARQUEE
        ))
        value("maxLines", fallback = Int.MAX_VALUE.toDouble())
        value("minLines", fallback = Int.MIN_VALUE.toDouble())
        value("textSize", fallback = 13.0)
        enum("textStyle", mapOf(
                "normal" to TextStyle.NORMAL,
                "bold" to TextStyle.BOLD
        ))
    }
}