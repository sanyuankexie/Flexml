package com.guet.flexbox.build

import com.guet.flexbox.enums.Orientation

object Scroller : Declaration() {
    override val dataBinding: DataBinding by DataBinding.create(CommonProps.dataBinding) {
        bool("scrollBarEnable")
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
        bool("fillViewport")
    }
}