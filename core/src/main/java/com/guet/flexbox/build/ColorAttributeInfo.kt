package com.guet.flexbox.build

import android.graphics.Color
import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetColor

internal class ColorAttributeInfo(scope: Map<String, Int>, fallback: Int?) : AttributeInfo<Int>(scope, fallback) {
    override fun cast(
            hostContext: HostContext,
            props: ELContext,
            raw: String
    ): Int? {
        return if (raw.isExpr) {
            props.scope(scope) {
                tryGetColor(raw, fallback)
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