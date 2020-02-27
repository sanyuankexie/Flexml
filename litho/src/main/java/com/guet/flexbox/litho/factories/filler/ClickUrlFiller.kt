package com.guet.flexbox.litho.factories.filler

import com.facebook.litho.ClickEvent
import com.facebook.litho.Component
import com.guet.flexbox.eventsystem.ExternalEventReceiver
import com.guet.flexbox.litho.LithoEventHandler

internal object ClickUrlFiller : PropFiller<Component.Builder<*>, ExternalEventReceiver> {
    override fun fill(
            c: Component.Builder<*>,
            display: Boolean,
            other: Map<String, Any>,
            value: ExternalEventReceiver
    ) {
        if (!other.containsKey("onClick")) {
            c.clickHandler(LithoEventHandler<ClickEvent>(value))
        }
    }
}