package com.guet.flexbox.databinding

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import com.guet.flexbox.HorizontalGravity
import com.guet.flexbox.VerticalGravity
import com.guet.flexbox.el.PropsELContext

internal object AbstractText : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        enum("verticalGravity", mapOf(
                "top" to VerticalGravity.TOP,
                "bottom" to VerticalGravity.BOTTOM,
                "center" to VerticalGravity.CENTER
        ))
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
        this["textStyle"] = object : AttributeInfo<Int>(mapOf(
                "normal" to Typeface.NORMAL,
                "bold" to Typeface.BOLD
        ), Typeface.NORMAL) {
            override fun cast(c: Context, props: PropsELContext, raw: String): Int? {
                return scope[raw] ?: fallback
            }
        }
    }
}