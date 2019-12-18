package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
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
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<NodeInfo>,
            selfVisibility: Boolean
    ): List<LockedInfo> {
        return parent?.transform(c, attrs, data, children, selfVisibility)
                ?: throw UnsupportedOperationException()
    }
}