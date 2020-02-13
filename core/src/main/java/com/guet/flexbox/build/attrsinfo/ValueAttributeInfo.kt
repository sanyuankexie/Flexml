package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.EventContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class ValueAttributeInfo(scope: Map<String, Float>, fallback: Float?)
    : AttributeInfo<Float>(scope, fallback) {
    override fun cast(eventContext: EventContext, data: ELContext, raw: String): Float? {
        return if (raw.isExpr) {
            data.scope(scope){
                tryGetValue(raw, fallback)
            }
        } else {
            scope[raw] ?: try {
                raw.toFloat()
            } catch (e: Exception) {
                fallback
            }
        }
    }
}