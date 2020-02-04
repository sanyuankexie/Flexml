package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.EventContext
import com.guet.flexbox.el.ELContext

internal abstract class AttributeInfo<T : Any>(
        protected val scope: (Map<String, T>) = emptyMap(),
        protected val fallback: T? = null
) {
    abstract fun cast(
            eventContext: EventContext,
            data: ELContext,
            raw: String
    ): T?
}
