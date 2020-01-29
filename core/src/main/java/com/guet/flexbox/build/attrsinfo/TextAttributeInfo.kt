package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

internal class TextAttributeInfo(scope: Map<String, String>, fallback: String?) : AttributeInfo<String>(scope, fallback) {
    override fun cast(hostContext: HostContext, data: ELContext, raw: String): String? {
        return data.scope(scope) {
            tryGetValue(raw, fallback)
        }
    }
}