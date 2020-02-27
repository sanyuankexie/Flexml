package com.guet.flexbox.litho.factories

import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.yoga.YogaEdge
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.eventsystem.ExternalEventReceiver
import com.guet.flexbox.litho.LithoEventHandler
import com.guet.flexbox.litho.factories.assign.Assignment
import com.guet.flexbox.litho.factories.assign.AttrsAssigns
import com.guet.flexbox.litho.toPx
import java.util.*

internal object CommonAssigns : ToComponent<Component.Builder<*>>() {

    override val attrsAssigns by AttrsAssigns
            .create<Component.Builder<*>> {
                pt("width", Component.Builder<*>::widthPx)
                pt("height", Component.Builder<*>::heightPx)
                pt("minWidth", Component.Builder<*>::minWidthPx)
                pt("maxWidth", Component.Builder<*>::maxWidthPx)
                pt("minHeight", Component.Builder<*>::minHeightPx)
                pt("maxHeight", Component.Builder<*>::maxHeightPx)
                value("flexGrow", Component.Builder<*>::flexGrow)
                value("flexShrink", Component.Builder<*>::flexShrink)
                enum("alignSelf", Component.Builder<*>::alignSelf)
                event("onClick", Component.Builder<*>::clickHandler)
                event("onVisible", Component.Builder<*>::visibleHandler)
                register("clickUrl", object : Assignment<Component.Builder<*>, ExternalEventReceiver> {
                    override fun assign(
                            c: Component.Builder<*>,
                            display: Boolean,
                            other: Map<String, Any>,
                            value: ExternalEventReceiver
                    ) {
                        if (!other.containsKey("onClick")) {
                            c.clickHandler(LithoEventHandler<ClickEvent>(value))
                        }
                    }
                })
                register("margin", object : Assignment<Component.Builder<*>, Float> {
                    override fun assign(c: Component.Builder<*>, display: Boolean, other: Map<String, Any>, value: Float) {
                        c.marginPx(YogaEdge.ALL, value.toPx())
                    }
                })
                register("padding", object : Assignment<Component.Builder<*>, Float> {
                    override fun assign(c: Component.Builder<*>, display: Boolean, other: Map<String, Any>, value: Float) {
                        c.paddingPx(YogaEdge.ALL, value.toPx())
                    }
                })
                arrayOf("Left", "Right", "Top", "Bottom").forEach { edge ->
                    val enum = YogaEdge.valueOf(edge.toUpperCase(Locale.US))
                    register("margin${edge}", object : Assignment<Component.Builder<*>, Float> {
                        override fun assign(
                                c: Component.Builder<*>,
                                display: Boolean,
                                other: Map<String, Any>,
                                value: Float
                        ) {
                            c.marginPx(
                                    enum,
                                    value.toPx()
                            )
                        }
                    })
                    register("padding${edge}", object : Assignment<Component.Builder<*>, Float> {
                        override fun assign(
                                c: Component.Builder<*>,
                                display: Boolean,
                                other: Map<String, Any>,
                                value: Float
                        ) {
                            c.paddingPx(
                                    enum,
                                    value.toPx()
                            )
                        }
                    })
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