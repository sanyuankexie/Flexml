package com.guet.flexbox.build

object Scroller : Declaration(CommonProps) {
    val ATTRIBUTE_RESOLVER_SET: DataBinding by create {
        bool("scrollBarEnable")
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
        bool("fillViewport")
    }
}