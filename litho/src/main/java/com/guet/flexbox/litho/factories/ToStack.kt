package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.filler.PropsFiller
import com.guet.flexbox.litho.widget.Stack

internal object ToStack : ToComponent<Stack.Builder>() {

    override val propsFiller = PropsFiller
            .use<Stack.Builder>(CommonProps)

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): Stack.Builder {
        return Stack.create(c)
    }

    override fun onInstallChildren(
            owner: Stack.Builder,
            visibility: Boolean,
            attrs: PropSet,
            children: List<Widget>
    ) {
        if (children.isEmpty()) {
            return
        }
        owner.children(children)
    }
}