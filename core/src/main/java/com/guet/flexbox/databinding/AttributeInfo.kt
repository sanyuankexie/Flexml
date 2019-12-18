package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.el.PropsELContext

internal abstract class AttributeInfo<T : Any>(
        protected val scope: (Map<String, T>) = emptyMap(),
        protected val fallback: T? = null
) {
    abstract fun cast(
            c: Context,
            props: PropsELContext,
            raw: String
    ): T?
}
