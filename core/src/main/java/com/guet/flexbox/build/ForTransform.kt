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
        val attrs = nodeInfo.attrs
        if (!attrs.isNullOrEmpty()) {
            val name = c.getValue<String>(attrs["name"] ?: error("must has attr 'name'"))
            val from = c.getValue<Int>(attrs["from"] ?: error("must has attr 'from'"))
            val to = c.getValue<Int>(attrs["to"] ?: error("must has attr 'to'"))
            val elements = nodeInfo.children
            return if (!elements.isNullOrEmpty()) {
                (from..to).map {
                    return@map c.scope(Collections.singletonMap(name, it)) {
                        elements.map { item ->
                            c.createFromElement(item, upperVisibility)
                        }.flatten()
                    }
                }.flatten()
            } else {
                emptyList()
            }
        } else {
            error("must has attr")
        }
    }
}
