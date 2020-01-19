package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class EnumAttributeInfo<V : Enum<V>>(
        scope: Map<String, Enum<V>>,
        fallback: Enum<V>?
) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(
            pageContext: HostingContext,
            props: ELContext,
            raw: String
    ): Enum<V>? {
        return if (raw.isExpr) {
            props.scope(scope) {
                tryGetValue(raw, fallback)
            }
        } else {
            scope[raw] ?: fallback
        }
    }
}