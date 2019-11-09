package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForEachBehavior : Behavior() {
    override fun doApply(
            c: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component.Builder<*>> {
        val name = c.requestValue<String>("var", attrs)
        val items = c.requestValue<List<Any>>("items", attrs)
        return items.map { item ->
            c.scope(Collections.singletonMap(name, item)) {
                children.map {
                    c.createFromElement(it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}