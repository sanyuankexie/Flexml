package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForBehavior : Behavior() {
    override fun onApply(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component.Builder<*>> {
        val name = c.requestValue<String>("var", attrs)
        val from = c.requestValue<Int>("from", attrs)
        val to = c.requestValue<Int>("to", attrs)
        return (from..to).map {
            return@map c.scope(Collections.singletonMap(name, it)) {
                children.map { item ->
                    c.createFromElement(item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
