package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.EventContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class EnumAttributeInfo<V : Enum<V>>(
        scope: Map<String, Enum<V>>,
        fallback: Enum<V>?
) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(
            eventContext: EventContext,
            data: ELContext,
            raw: String
    ): Enum<V>? {
        return if (raw.isExpr) {
            data.scope(scope) {
                tryGetValue(raw, fallback)
            }
        } else {
            scope[raw] ?: fallback
        }
    }
}