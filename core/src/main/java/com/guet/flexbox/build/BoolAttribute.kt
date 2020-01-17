package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal class BoolAttribute(scope: Map<String, Boolean>, fallback: Boolean?) : AttributeInfo<Boolean>(scope, fallback) {
    override fun cast(
            pageContext: HostingContext,
            props: PropsELContext,
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