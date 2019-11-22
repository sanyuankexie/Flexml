package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo

internal object IfBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            buildContext: BuildContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        return if (buildContext.requestValue("test", attrs)) {
            return children.map {
                c.createFromElement(buildContext, it, upperVisibility)
            }.flatten()
        } else {
            emptyList()
        }
    }
}