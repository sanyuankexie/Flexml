package com.guet.flexbox.databinding

import com.guet.flexbox.PageContext
import com.guet.flexbox.el.PropsELContext

internal class EnumAttribute<V : Enum<V>>(
        scope: Map<String, Enum<V>>,
        fallback: Enum<V>?
) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(pageContext: PageContext, props: PropsELContext, raw: String): Enum<V>? {
        return if (raw.isExpr) {
            props.tryGetValue(raw, fallback)
        } else {
            scope[raw] ?: fallback
        }
    }
}