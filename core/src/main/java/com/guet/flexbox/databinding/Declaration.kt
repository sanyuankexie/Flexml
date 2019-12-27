package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ToComponent
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
            to: ToComponent<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        return parent?.transform(c, to, type, attrs, data, children, upperVisibility)
                ?: throw UnsupportedOperationException()
    }
}