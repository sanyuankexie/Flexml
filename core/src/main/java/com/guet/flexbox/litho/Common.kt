package com.guet.flexbox.litho

import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.EventHandler
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import java.util.*

internal object Common : ToComponent<Component.Builder<*>>() {

    override val attributeSet: AttributeSet<Component.Builder<*>> by create {
        register("width") { _, _, value: Double ->
            widthPx(value.toPx())
        }
        register("height") { _, _, value: Double ->
            heightPx(value.toPx())
        }
        register("minWidth") { _, _, value: Double ->
            minWidthPx(value.toPx())
        }
        register("maxWidth") { _, _, value: Double ->
            maxWidthPx(value.toPx())
        }
        register("minHeight") { _, _, value: Double ->
            minHeightPx(value.toPx())
        }
        register("maxWidth") { _, _, value: Double ->
            maxHeightPx(value.toPx())
        }
        register("flexGrow") { _, _, value: Double ->
            flexGrow(value.toFloat())
        }
        register("flexShrink") { _, _, value: Double ->
            flexShrink(value.toFloat())
        }
        register("alignSelf") { _, _, value: YogaAlign ->
            alignSelf(value)
        }
        register("margin") { _, _, value: Double ->
            marginPx(YogaEdge.ALL, value.toPx())
        }
        register("padding") { _, _, value: Double ->
            paddingPx(YogaEdge.ALL, value.toPx())
        }
        arrayOf("Left", "Right", "Top", "Bottom").map { edge ->
            edge to YogaEdge.valueOf(edge.toUpperCase(Locale.US))
        }.forEach { edge ->
            register("margin${edge.first}") { _, _, value: Double ->
                marginPx(edge.second, value.toPx())
            }
            register("padding${edge.first}") { _, _, value: Double ->
                paddingPx(edge.second, value.toPx())
            }
        }
        register("clickUrl") { _, other, value: EventHandler<ClickEvent> ->
            if (!other.containsKey("onClick")) {
                clickHandler(value)
            }
        }
        register("onClick") { _, _, value: EventHandler<ClickEvent> ->
            clickHandler(value)
        }
        register("shadowElevation") { _, _, value: Double ->
            shadowElevationPx(value.toFloat())
        }
    }

    override fun create(c: ComponentContext, visibility: Boolean, attrs: Map<String, Any>): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}