package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.PropsELContext
import java.util.*

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
            return@map data.scope(Collections.singletonMap(name, it)) {
                children.map { item ->
                    data.inflate(c, item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
