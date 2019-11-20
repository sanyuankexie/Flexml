package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForEachBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = buildContext.requestValue<String>("var", attrs)
        val items = buildContext.requestValue<List<Any>>("items", attrs)
        return items.map { item ->
            buildContext.scope(Collections.singletonMap(name, item)) {
                children.map {
                    c.createFromElement(buildContext, it, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}