package com.guet.flexbox.databinding

import com.guet.flexbox.el.PropsELContext

internal class TextAttribute(scope: Map<String, String>, fallback: String?) : AttributeInfo<String>(scope, fallback) {
    override fun cast(props: PropsELContext, raw: String): String? {
        return props.scope(scope) {
            props.tryGetValue(raw, fallback)
        }
    }
}