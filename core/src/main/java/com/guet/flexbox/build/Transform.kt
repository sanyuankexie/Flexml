package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.PropsELContext

internal interface Transform {

    fun transform(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>?

}