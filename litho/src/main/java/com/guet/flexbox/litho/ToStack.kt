package com.guet.flexbox.litho

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.widget.Stack

internal object ToStack : ToComponent<Stack.Builder>(Common) {

    override val attributeAssignSet: AttributeAssignSet<Stack.Builder>
        get() = emptyMap()

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Stack.Builder {
        return Stack.create(c)
    }

    override fun onInstallChildren(
            owner: Stack.Builder,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        if (children.isEmpty()) {
            return
        }
        owner.children(children.map {
            it
        })
    }
}