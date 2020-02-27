package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.AttributeSet
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.assign.AttrsAssigns
import com.guet.flexbox.litho.widget.Banner

internal object ToBanner : ToComponent<Banner.Builder>() {
    override val attrsAssigns by AttrsAssigns
            .create<Banner.Builder>(CommonAssigns.attrsAssigns) {
                bool("isCircular", Banner.Builder::isCircular)
                value("timeSpan", Banner.Builder::timeSpan)
                enum("orientation", Banner.Builder::orientation)
                bool("indicatorEnable", Banner.Builder::indicatorEnable)
                pt("indicatorSize", Banner.Builder::indicatorSizePx)
                pt("indicatorHeight", Banner.Builder::indicatorHeightPx)
                text("indicatorSelected", Banner.Builder::indicatorSelected)
                text("indicatorUnselected", Banner.Builder::indicatorUnselected)
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
            children: List<Widget>
    ) {
        owner.children(children)
    }

}