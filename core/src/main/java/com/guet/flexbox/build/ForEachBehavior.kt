package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForEachBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            pager: PagerContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = pager.requestValue<String>("var", attrs)
        val items = pager.requestValue<List<Any>>("items", attrs)
        return items.map { item ->
            pager.scope(Collections.singletonMap(name, item)) {
                children.map {
                    pager.inflate(c, it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}