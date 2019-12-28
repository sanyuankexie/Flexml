package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.widget.Stack

internal object ToStack : ToComponent<Stack.Builder>(Common) {

    override val attributeSet: AttributeSet<Stack.Builder>
        get() = emptyMap()

    override fun create(
            c: ComponentContext,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>
    ): Stack.Builder {
        return Stack.create(c)
    }

    override fun onInstallChildren(
            owner: Stack.Builder,
            type: String,
            visibility: Boolean,
            attrs: Map<String, Any>,
            children: List<Component>
    ) {
        if (children.isEmpty()) {
            return
        }
        owner.children(children)
    }
}