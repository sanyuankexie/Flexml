package com.guet.flexbox.databinding

import com.guet.flexbox.el.PropsELContext

internal class ValueAttribute(scope: Map<String, Double>, fallback: Double?)
    : AttributeInfo<Double>(scope, fallback) {
    override fun cast(props: PropsELContext, raw: String): Double? {
        return if (raw.isExpr) {
            props.tryGetValue(raw, fallback)
        } else {
            scope[raw] ?: try {
                raw.toDouble()
            } catch (e: Exception) {
                fallback
            }
        }
    }
}