package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo

internal interface Transform {

    fun transform(
            c: ComponentContext,
            buildContext: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>

}