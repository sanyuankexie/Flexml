package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import java.util.*

internal object ForBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        val name = buildContext.requestValue<String>("var", attrs)
        val from = buildContext.requestValue<Int>("from", attrs)
        val to = buildContext.requestValue<Int>("to", attrs)
        return (from..to).map {
            return@map buildContext.scope(Collections.singletonMap(name, it)) {
                children.map { item ->
                    c.createFromElement(buildContext, item, upperVisibility)
                }.flatten()
            }
        }.flatten()
    }
}
