package com.luke.skywalker.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.luke.skywalker.NodeInfo
import com.luke.skywalker.el.PropsELContext

internal interface Transform {

    fun transform(
            c: ComponentContext,
            data: PropsELContext,
            nodeInfo: NodeInfo,
            upperVisibility: Int
    ): List<Component>?

}