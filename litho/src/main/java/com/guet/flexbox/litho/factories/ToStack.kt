package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.resolve.AttrsAssigns
import com.guet.flexbox.litho.widget.Stack

internal object ToStack : ToComponent<Stack.Builder>() {

    override val attrsAssigns = AttrsAssigns
            .use<Stack.Builder>(CommonAssigns.attrsAssigns)

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
            children: List<Widget>
    ) {
        if (children.isEmpty()) {
            return
        }
        owner.children(children.map {
            it
        })
    }
}