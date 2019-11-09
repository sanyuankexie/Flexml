package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.NodeInfo

internal abstract class Behavior : Transform {

    final override fun transform(
            c: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component.Builder<*>> {
        val attrs = nodeInfo.attrs
        val elements = nodeInfo.children
        if (!attrs.isNullOrEmpty()) {
            if (elements.isNullOrEmpty()) {
                return emptyList()
            }
            return doApply(c, attrs, elements, upperVisibility)
        } else {
            error("must has attr test")
        }
    }

    protected abstract fun doApply(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component.Builder<*>>

    protected inline fun <reified T : Any> BuildContext.requestValue(
            name: String,
            attrs: Map<String, String>
    ): T {
        return getValue(attrs[name] ?: error("request attr '$name'"))
    }
}
