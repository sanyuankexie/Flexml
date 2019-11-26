package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo

internal abstract class Behavior : Transform {

    final override fun transform(
            c: ComponentContext,
            pager: PagerContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>? {
        val attrs = nodeInfo.attrs
        val elements = nodeInfo.children
        if (!attrs.isNullOrEmpty()) {
            if (elements.isNullOrEmpty()) {
                return emptyList()
            }
            return onApply(c, pager, attrs, elements, upperVisibility)
        } else {
            error("must has attr")
        }
    }

    protected abstract fun onApply(
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component>?
}
