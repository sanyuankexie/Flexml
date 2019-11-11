package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.NodeInfo

internal interface Transform {
    fun transform(
            c: BuildContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>
}