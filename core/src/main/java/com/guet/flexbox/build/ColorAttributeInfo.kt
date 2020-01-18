package com.guet.flexbox.build

import android.graphics.Color
import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal class ColorAttributeInfo(scope: Map<String, Int>, fallback: Int?) : AttributeInfo<Int>(scope, fallback) {
    override fun cast(
            pageContext: HostingContext,
            props: PropsELContext,
            raw: String
    ): Int? {
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