package com.guet.flexbox.litho.factories

import com.facebook.litho.ComponentContext
import com.guet.flexbox.build.PropSet
import com.guet.flexbox.litho.Widget
import com.guet.flexbox.litho.factories.filler.PropsFiller
import com.guet.flexbox.litho.widget.Banner

internal object ToBanner : ToComponent<Banner.Builder>() {
    override val propsFiller by PropsFiller
            .create<Banner.Builder>(CommonProps) {
                bool("isCircular", Banner.Builder::isCircular)
                bool("indicatorEnable", Banner.Builder::indicatorEnable)
                value("timeSpan", Banner.Builder::timeSpan)
                enum("orientation", Banner.Builder::orientation)
                pt("indicatorSize", Banner.Builder::indicatorSizePx)
                pt("indicatorHeight", Banner.Builder::indicatorHeightPx)
                text("indicatorSelected", Banner.Builder::indicatorSelected)
                text("indicatorUnselected", Banner.Builder::indicatorUnselected)
            }

    override fun create(
            c: ComponentContext,
            visibility: Boolean,
            attrs: PropSet
    ): Banner.Builder {
        return Banner.create(c)
    }

    override fun onInstallChildren(
            owner: Banner.Builder,
            visibility: Boolean,
            attrs: PropSet,
            children: List<Widget>
    ) {
        owner.children(children)
    }

}