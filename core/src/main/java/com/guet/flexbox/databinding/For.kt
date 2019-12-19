package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.data.LayoutNode
import com.guet.flexbox.data.RenderNode
import com.guet.flexbox.el.PropsELContext

internal object For : Declaration() {
    override val attributeSet: AttributeSet by create {
        text("var")
        value("from")
        value("to")
    }

    override fun transform(
            c: Context,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<LayoutNode>,
            selfVisibility: Boolean
    ): List<RenderNode> {
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Double).toInt()
        val to = (attrs.getValue("to") as Double).toInt()
        return (from..to).map { index ->
            data.scope(mapOf(name to index)) {
                children.mapNotNull {
                    DataBindingUtils.bindNode(c, it, data, selfVisibility)
                }
            }
        }.flatten()
    }
}