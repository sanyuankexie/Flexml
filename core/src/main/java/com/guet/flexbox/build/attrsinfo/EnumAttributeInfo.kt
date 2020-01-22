package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.HostContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class EnumAttributeInfo<V : Enum<V>>(
        scope: Map<String, Enum<V>>,
        fallback: Enum<V>?
) : AttributeInfo<Enum<V>>(scope, fallback) {
    override fun cast(
            hostContext: HostContext,
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