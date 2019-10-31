package com.guet.flexbox.build

import com.facebook.litho.Component
import com.guet.flexbox.widget.Frame

internal object FrameFactory : WidgetFactory<Frame.Builder>() {

    override fun create(
            c: BuildContext,
            attrs: Map<String, String>): Frame.Builder {
        return Frame.create(c.componentContext)
    }

    override fun Frame.Builder.applyChildren(c: BuildContext,
                                             attrs: Map<String, String>,
                                             children: List<Component.Builder<*>>) {
        children.forEach {
            child(it.build())
        }
    }
}