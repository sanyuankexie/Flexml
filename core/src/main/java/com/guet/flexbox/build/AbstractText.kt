package com.guet.flexbox.build

import android.graphics.Typeface
import android.text.Layout
import android.text.TextUtils
import com.facebook.litho.widget.VerticalGravity

object AbstractText : Declaration(Common) {

    override val attributeInfoSet: AttributeInfoSet by create {
        enum("verticalGravity", mapOf(
                "top" to VerticalGravity.TOP,
                "bottom" to VerticalGravity.BOTTOM,
                "center" to VerticalGravity.CENTER
        ))
        @Suppress("NewApi")
        enum("horizontalGravity", mapOf(
                "left" to Layout.Alignment.ALIGN_LEFT,
                "right" to Layout.Alignment.ALIGN_LEFT,
                "center" to Layout.Alignment.ALIGN_CENTER
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
        val scope = mapOf(
                "normal" to Typeface.NORMAL,
                "bold" to Typeface.BOLD
        )
        typed("textStyle") { _,
                             _,
                             raw: String ->
            scope[raw]
        }
    }
}