package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.content.DynamicNode
import com.guet.flexbox.content.RenderNode
import com.guet.flexbox.el.PropsELContext

internal abstract class Declaration(private val parent: Declaration? = null) {

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
            c: Context,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<DynamicNode>,
            upperVisibility: Boolean
    ): List<RenderNode> {
        return parent?.transform(c, type, attrs, data, children, upperVisibility)
                ?: throw UnsupportedOperationException()
    }
}