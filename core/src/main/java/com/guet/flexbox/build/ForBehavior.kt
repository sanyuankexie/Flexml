package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            dataBinding: DataContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = dataBinding.requestValue<String>("var", attrs)
        val from = dataBinding.requestValue<Int>("from", attrs)
        val to = dataBinding.requestValue<Int>("to", attrs)
        return (from..to).map {
            return@map dataBinding.scope(Collections.singletonMap(name, it)) {
                children.map { item ->
                    c.createFromElement(dataBinding, item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
