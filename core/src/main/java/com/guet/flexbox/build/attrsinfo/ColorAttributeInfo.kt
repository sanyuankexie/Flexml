package com.guet.flexbox.build.attrsinfo

import android.graphics.Color
import com.guet.flexbox.HostContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetColor

internal class ColorAttributeInfo(scope: Map<String, Int>, fallback: Int?) : AttributeInfo<Int>(scope, fallback) {
    override fun cast(
            hostContext: HostContext,
            data: ELContext,
            raw: String
    ): Int? {
        return if (raw.isExpr) {
            data.scope(scope) {
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