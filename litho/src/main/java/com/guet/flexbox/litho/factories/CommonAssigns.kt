package com.guet.flexbox.litho.factories

import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.VisibleEvent
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.EventHandler
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.litho.event.EventHandlerWrapper
import com.guet.flexbox.litho.toPx
import com.guet.flexbox.litho.toPxFloat
import java.util.*

internal object CommonAssigns : ToComponent<Component.Builder<*>>() {

    override val attributeAssignSet: AttributeAssignSet<Component.Builder<*>> by create {
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
        register("alignSelf") { _, _, value: FlexAlign ->
            alignSelf(value.mapping())
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
        register("clickUrl") { _, other, value: EventHandler ->
            if (!other.containsKey("onClick")) {
                clickHandler(EventHandlerWrapper<ClickEvent>(value))
            }
        }
        register("onClick") { _, _, value: EventHandler ->
            clickHandler(EventHandlerWrapper<ClickEvent>(value))
        }
        register("onVisible") { _, _, value: EventHandler ->
            visibleHandler(EventHandlerWrapper<VisibleEvent>(value))
        }
        register("shadowElevation") { _, _, value: Double ->
            shadowElevationPx(value.toPxFloat())
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}