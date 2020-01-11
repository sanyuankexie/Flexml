package com.guet.flexbox.litho

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.litho.widget.Banner

internal object ToBanner : ToComponent<Banner.Builder>(Common) {
    override val attributeSet: AttributeSet<Banner.Builder> by create {
        register("isCircular") { _, _, value: Boolean ->
            isCircular(value)
        }
        register("timeSpan") { _, _, value: Double ->
            timeSpan(value.toLong())
        }
    }

    override fun create(
            c: ComponentContext,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>
    ): Banner.Builder {
        return Banner.create(c)
    }

    override fun onInstallChildren(
            owner: Banner.Builder,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>,
            children: List<Component>
    ) {
        owner.children(children)
    }
}