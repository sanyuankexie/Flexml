package com.guet.flexbox.litho.factories

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.factories.filler.ClickUrlFiller
import com.guet.flexbox.litho.factories.filler.PropsFiller

internal object CommonProps : ToComponent<Component.Builder<*>>() {

    override val propsFiller by PropsFiller
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
                register("clickUrl", ClickUrlFiller)
                edges("margin", Component.Builder<*>::marginPx)
                edges("padding", Component.Builder<*>::paddingPx)
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): Component.Builder<*> {
        throw UnsupportedOperationException()
    }
}