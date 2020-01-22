package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class BoolAttributeInfo(scope: Map<String, Boolean>, fallback: Boolean?) : AttributeInfo<Boolean>(scope, fallback) {
    override fun cast(
            hostContext: HostContext,
            props: ELContext,
            raw: String
    ): Boolean? {
        return if (raw.isExpr) {
            props.scope(scope){
                tryGetValue(raw, fallback)
            }
        } else {
            try {
                raw.toBoolean()
            } catch (e: Exception) {
                fallback
            }
        }
    }
}