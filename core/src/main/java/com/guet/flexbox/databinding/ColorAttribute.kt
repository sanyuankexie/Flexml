package com.guet.flexbox.databinding

import android.content.Context
import android.graphics.Color
import com.guet.flexbox.el.PropsELContext

internal class ColorAttribute(scope: Map<String, Int>, fallback: Int?) : AttributeInfo<Int>(scope, fallback) {
    override fun cast(c: Context, props: PropsELContext, raw: String): Int? {
        return if (raw.isExpr) {
            props.scope(scope) {
                props.tryGetColor(raw, fallback)
            }
        } else {
            try {
                Color.parseColor(raw)
            } catch (e: Exception) {
                fallback
            }
        }
    }
}