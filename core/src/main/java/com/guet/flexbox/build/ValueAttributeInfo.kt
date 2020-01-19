package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class ValueAttributeInfo(scope: Map<String, Double>, fallback: Double?)
    : AttributeInfo<Double>(scope, fallback) {
    override fun cast(pageContext: HostingContext, props: ELContext, raw: String): Double? {
        return if (raw.isExpr) {
            props.scope(scope){
                props.tryGetValue(raw, fallback)
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