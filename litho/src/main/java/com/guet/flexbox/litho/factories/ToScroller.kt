package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.ChildComponent
import com.guet.flexbox.litho.widget.Scroller

internal object ToScroller : ToComponent<Scroller.Builder>(CommonAssigns) {

    override val attributeAssignSet: AttributeAssignSet<Scroller.Builder> by create {
        register("scrollBarEnable") { _, _, value: Boolean ->

        }
        register("fillViewport") { _, _, value: Boolean ->
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Scroller.Builder {
        return Scroller.create(c)
    }

    override fun onInstallChildren(
            owner: Scroller.Builder,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        if (children.isNullOrEmpty()) {
            return
        }
        owner.component(children.single())
    }
}