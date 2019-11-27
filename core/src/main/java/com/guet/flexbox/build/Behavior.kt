package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.PropsELContext

internal abstract class Behavior : Transform {

    final override fun transform(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>? {
        val attrs = nodeInfo.attrs
        val elements = nodeInfo.children
        if (!attrs.isNullOrEmpty()) {
            if (elements.isNullOrEmpty()) {
                return emptyList()
            }
            return onApply(c, data, attrs, elements, upperVisibility)
        } else {
            error("must has attr")
        }
    }

    protected abstract fun onApply(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component>?
}
