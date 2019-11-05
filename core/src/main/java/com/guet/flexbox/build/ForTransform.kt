package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForTransform : Transform {
    override fun transform(
            c: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component.Builder<*>> {
        val attrs = nodeInfo.attrs ?: emptyMap()
        val name = c.getValue(attrs["name"] ?: error("must has attr 'name'"), String::class.java)
        val from = c.getValue(attrs["from"] ?: error("must has attr 'from'"), Int::class.java)
        val to = c.getValue(attrs["to"] ?: error("must has attr 'to'"), Int::class.java)
        val elements = nodeInfo.children
        return (from..to).map {
            return@map c.scope(Collections.singletonMap(name, it)) {
                elements?.map { item ->
                    c.createFromElement(item, upperVisibility)
                }?.flatten() ?: emptyList()
            }
        }.flatten()
    }
}
