package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.EventContext
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class ValueAttributeInfo(scope: Map<String, Double>, fallback: Double?)
    : AttributeInfo<Double>(scope, fallback) {
    override fun cast(eventContext: EventContext, data: ELContext, raw: String): Double? {
        return if (raw.isExpr) {
            data.scope(scope){
                tryGetValue(raw, fallback)
            }
        } else {
            scope[raw] ?: try {
                raw.toDouble()
            } catch (e: Exception) {
                fallback
            }
        }
    }
}