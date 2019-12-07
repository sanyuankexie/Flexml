package com.luke.skywalker.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.luke.skywalker.NodeInfo
import com.luke.skywalker.el.PropsELContext

internal object ForEachBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = data.requestValue<String>("var", attrs)
        val items = data.requestValue<List<Any>>("items", attrs)
        return items.map { item ->
            data.scope(mapOf(name to item)) {
                children.map {
                    data.inflate(c, it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}