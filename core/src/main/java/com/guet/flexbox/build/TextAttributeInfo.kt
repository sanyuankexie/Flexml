package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal class TextAttributeInfo(scope: Map<String, String>, fallback: String?) : AttributeInfo<String>(scope, fallback) {
    override fun cast(pageContext: HostingContext, props: PropsELContext, raw: String): String? {
        return props.scope(scope) {
            props.tryGetValue(raw, fallback)
        }
    }
}