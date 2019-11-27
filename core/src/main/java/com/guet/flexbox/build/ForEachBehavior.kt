package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.PropsELContext
import java.util.*

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
            data.scope(Collections.singletonMap(name, item)) {
                children.map {
                    data.inflate(c, it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}