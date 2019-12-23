package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.ContentNode
import com.guet.flexbox.build.ComponentAdapt
import com.guet.flexbox.el.PropsELContext

internal abstract class Declaration(
        private val parent: Declaration? = null
) {

    protected abstract val attributeSet: AttributeSet

    operator fun get(name: String): AttributeInfo<*>? {
        val v = attributeSet[name]
        if (v != null) {
            return v
        }
        if (parent != null) {
            return parent[name]
        }
        return null
    }

    internal open fun transform(
            c: ComponentContext,
            adapt: ComponentAdapt<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<ContentNode>,
            upperVisibility: Boolean
    ): List<Component> {
        return parent?.transform(c, adapt, type, attrs, data, children, upperVisibility)
                ?: throw UnsupportedOperationException()
    }
}