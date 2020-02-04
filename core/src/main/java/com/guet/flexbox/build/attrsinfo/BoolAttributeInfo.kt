package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.EventContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class BoolAttributeInfo(scope: Map<String, Boolean>, fallback: Boolean?) : AttributeInfo<Boolean>(scope, fallback) {
    override fun cast(
            eventContext: EventContext,
            data: ELContext,
            raw: String
    ): Boolean? {
        return if (raw.isExpr) {
            data.scope(scope){
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