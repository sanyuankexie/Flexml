package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.WidgetInfo

internal interface Transform {
    fun transform(c: BuildContext,
                  widgetInfo: WidgetInfo,
                  children: List<Component.Builder<*>>)
            : List<Component.Builder<*>>
}