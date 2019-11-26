package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = pager.requestValue<String>("var", attrs)
        val from = pager.requestValue<Int>("from", attrs)
        val to = pager.requestValue<Int>("to", attrs)
        return (from..to).map {
            return@map pager.scope(Collections.singletonMap(name, it)) {
                children.map { item ->
                    pager.inflate(c, item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
