package com.guet.flexbox.litho.factories

import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.VisibleEvent
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.FlexAlign
import com.guet.flexbox.eventsystem.EventAdapter
import com.guet.flexbox.litho.LithoEventAdapter
import com.guet.flexbox.litho.resolve.AttributeAssignSet
import com.guet.flexbox.litho.resolve.mapping
import com.guet.flexbox.litho.toPx
import java.util.*

internal object CommonAssigns : ToComponent<Component.Builder<*>>() {

    override val attributeAssignSet: AttributeAssignSet<Component.Builder<*>> by com.guet.flexbox.litho.resolve.create {
        register("width") { _, _, value: Float ->
            widthPx(value.toPx())
        }
        register("height") { _, _, value: Float ->
            heightPx(value.toPx())
        }
        register("minWidth") { _, _, value: Float ->
            minWidthPx(value.toPx())
        }
        register("maxWidth") { _, _, value: Float ->
            maxWidthPx(value.toPx())
        }
        register("minHeight") { _, _, value: Float ->
            minHeightPx(value.toPx())
        }
        register("maxWidth") { _, _, value: Float ->
            maxHeightPx(value.toPx())
        }
        register("flexGrow") { _, _, value: Float ->
            flexGrow(value)
        }
        register("flexShrink") { _, _, value: Float ->
            flexShrink(value)
        }
        register("alignSelf") { _, _, value: FlexAlign ->
            alignSelf(value.mapping())
        }
        register("margin") { _, _, value: Float ->
            marginPx(YogaEdge.ALL, value.toPx())
        }
        register("padding") { _, _, value: Float ->
            paddingPx(YogaEdge.ALL, value.toPx())
        }
        arrayOf("Left", "Right", "Top", "Bottom").map { edge ->
            edge to YogaEdge.valueOf(edge.toUpperCase(Locale.US))
        }.forEach { edge ->
            register("margin${edge.first}") { _, _, value: Float ->
                marginPx(edge.second, value.toPx())
            }
            register("padding${edge.first}") { _, _, value: Float ->
                paddingPx(edge.second, value.toPx())
            }
        }
        register("clickUrl") { _, other, value: EventAdapter ->
            if (!other.containsKey("onClick")) {
                clickHandler(LithoEventAdapter<ClickEvent>(value))
            }
        }
        register("onClick") { _, _, value: EventAdapter ->
            clickHandler(LithoEventAdapter<ClickEvent>(value))
        }
        register("onVisible") { _, _, value: EventAdapter ->
            visibleHandler(LithoEventAdapter<VisibleEvent>(value))
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