package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class TextAttributeInfo(scope: Map<String, String>, fallback: String?) : AttributeInfo<String>(scope, fallback) {
    override fun cast(hostContext: HostContext, props: ELContext, raw: String): String? {
        return props.scope(scope) {
            tryGetValue(raw, fallback)
        }
    }
}