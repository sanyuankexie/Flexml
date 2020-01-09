package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

internal class TextAttribute(scope: Map<String, String>, fallback: String?) : AttributeInfo<String>(scope, fallback) {
    override fun cast(pageContext: PageContext, props: PropsELContext, raw: String): String? {
        return props.scope(scope) {
            props.tryGetValue(raw, fallback)
        }
    }
}