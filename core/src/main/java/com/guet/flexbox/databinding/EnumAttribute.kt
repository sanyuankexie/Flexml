package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.el.PropsELContext

internal class EnumAttribute<V : Enum<V>>(scope: Map<String, Enum<V>>, fallback: Enum<V>?) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(c: Context, props: PropsELContext, raw: String): Enum<V>? {
        return if (raw.isExpr) {
            props.tryGetValue(raw, fallback)
        } else {
            scope[raw] ?: fallback
        }
    }
}