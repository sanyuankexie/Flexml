package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.build.isExpr
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue
import com.guet.flexbox.transaction.PageContext

internal class ValueAttributeInfo(scope: Map<String, Float>, fallback: Float?)
    : AttributeInfo<Float>(scope, fallback) {
    override fun cast(pageContext: PageContext, data: ELContext, raw: String): Float? {
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