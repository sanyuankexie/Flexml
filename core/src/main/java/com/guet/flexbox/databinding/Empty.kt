package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.el.PropsELContext

internal object Empty : Declaration(Common) {
    override val attributeSet: AttributeSet
        get() = emptyMap()

    override fun transform(
            c: Context,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<LayoutNode>,
            upperVisibility: Boolean
    ): List<RenderNode> {
        return super.transform(c, type, attrs, data, children, false)
    }
}