package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.ELContext

internal abstract class AttributeInfo<T : Any>(
        protected val scope: (Map<String, T>) = emptyMap(),
        protected val fallback: T? = null
) {
    abstract fun cast(
            pageContext: HostingContext,
            props: ELContext,
            raw: String
    ): T?
}
