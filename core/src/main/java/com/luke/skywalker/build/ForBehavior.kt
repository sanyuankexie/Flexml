package com.luke.skywalker.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.luke.skywalker.NodeInfo
import com.luke.skywalker.el.PropsELContext

internal object ForBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = data.requestValue<String>("var", attrs)
        val from = data.requestValue<Int>("from", attrs)
        val to = data.requestValue<Int>("to", attrs)
        return (from..to).map {
            return@map data.scope(mapOf(name to it)) {
                children.map { item ->
                    data.inflate(c, item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
