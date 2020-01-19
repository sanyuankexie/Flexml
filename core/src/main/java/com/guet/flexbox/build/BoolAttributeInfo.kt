package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class BoolAttributeInfo(scope: Map<String, Boolean>, fallback: Boolean?) : AttributeInfo<Boolean>(scope, fallback) {
    override fun cast(
            pageContext: HostingContext,
            props: ELContext,
            raw: String
    ): Boolean? {
        return if (raw.isExpr) {
            props.scope(scope){
                props.tryGetValue(raw, fallback)
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