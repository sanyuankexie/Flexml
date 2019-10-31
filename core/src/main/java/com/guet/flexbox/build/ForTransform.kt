package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.WidgetInfo
import java.util.*

internal object ForTransform : Transform {
    override fun transform(
            c: BuildContext,
            widgetInfo: WidgetInfo,
            children: List<Component.Builder<*>>): List<Component.Builder<*>> {
        val attrs = widgetInfo.attrs ?: emptyMap()
        val name = c.getValue(attrs["type"] ?: error("must has attr 'type'"), String::class.java)
        val from = c.getValue(attrs["from"] ?: error("must has attr 'from'"), Int::class.java)
        val to = c.getValue(attrs["to"] ?: error("must has attr 'to'"), Int::class.java)
        val elements = widgetInfo.children
        return (from..to).map {
            return@map c.scope(Collections.singletonMap(name, it)) {
                elements?.map { item ->
                    c.createFromElement(item)
                }?.flatten() ?: emptyList()
            }
        }.flatten()
    }
}
