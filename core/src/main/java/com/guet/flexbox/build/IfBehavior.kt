package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.PropsELContext

internal object IfBehavior : Behavior() {
    override fun onApply(
            c: ComponentContext,
            data: PropsELContext,
            attrs: Map<String, String>,
            children: List<NodeInfo>,
            upperVisibility: Int
    ): List<Component> {
        return if (data.requestValue("test", attrs)) {
            return children.map {
                data.inflate(c, it, upperVisibility)
            }.flatten()
        } else {
            emptyList()
        }
    }
}