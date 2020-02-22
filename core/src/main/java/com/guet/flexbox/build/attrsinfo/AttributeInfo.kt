package com.guet.flexbox.build.attrsinfo

import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.el.ELContext

internal abstract class AttributeInfo<T : Any>(
        protected val scope: (Map<String, T>) = emptyMap(),
        protected val fallback: T? = null
) {
    abstract fun cast(
            pageContext: PageContext,
            data: ELContext,
            raw: String
    ): T?
}
