package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.Row
import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaPositionType

internal object FrameFactory : WidgetFactory<Row.Builder>() {

    init {
        value("flexGrow") {
            this.flexGrow(if (it <= 1) 1f else it.toFloat())
        }
    }

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>): Row.Builder {
        return Row.create(c.componentContext).apply {
            applyDefault(c, attrs)
        }
    }

    override fun Row.Builder
            .applyChildren(c: BuildContext,
                           attrs: Map<String, String>,
                           children: List<Component.Builder<*>>) {
        children.forEach {
            child(it.positionType(YogaPositionType.ABSOLUTE)
                    .positionPx(YogaEdge.TOP, 0)
                    .positionPx(YogaEdge.LEFT, 0))
        }
    }
}