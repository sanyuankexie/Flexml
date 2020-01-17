package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal class EnumAttribute<V : Enum<V>>(
        scope: Map<String, Enum<V>>,
        fallback: Enum<V>?
) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(pageContext: HostingContext, props: PropsELContext, raw: String): Enum<V>? {
        return if (raw.isExpr) {
            props.scope(scope) {
                props.tryGetValue(raw, fallback)
            }
        } else {
            scope[raw] ?: fallback
        }
    }
}