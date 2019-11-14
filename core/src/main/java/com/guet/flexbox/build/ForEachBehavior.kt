package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForEachBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            dataBinding: DataBinding,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = dataBinding.requestValue<String>("var", attrs)
        val items = dataBinding.requestValue<List<Any>>("items", attrs)
        return items.map { item ->
            dataBinding.scope(Collections.singletonMap(name, item)) {
                children.map {
                    c.createFromElement(dataBinding, it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}