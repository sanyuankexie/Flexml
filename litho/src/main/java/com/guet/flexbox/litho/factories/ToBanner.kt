package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.enums.Orientation
import com.guet.flexbox.litho.ChildComponent
import com.guet.flexbox.litho.toPx
import com.guet.flexbox.litho.widget.Banner

internal object ToBanner : ToComponent<Banner.Builder>(CommonAssigns) {
    override val attributeAssignSet: AttributeAssignSet<Banner.Builder> by create {
        register("isCircular") { _, _, value: Boolean ->
            isCircular(value)
        }
        register("timeSpan") { _, _, value: Float ->
            timeSpan(value.toLong())
        }
        register("orientation") { _, _, value: Orientation ->
            orientation(value)
        }
        register("indicatorsHeight") { _, _, value: Float ->
            indicatorHeightPx(value.toPx())
        }
        register("indicatorEnable") { _, _, value: Boolean ->
            indicatorEnable(value)
        }
    }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: AttributeSet
    ): Banner.Builder {
        return Banner.create(c)
    }

    override fun onInstallChildren(
            owner: Banner.Builder,
            visibility: Boolean,
            attrs: AttributeSet,
            children: List<ChildComponent>
    ) {
        owner.children(children.map {
            it
        })
    }
}