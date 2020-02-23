package com.guet.flexbox.build

import com.guet.flexbox.enums.Orientation

object Banner : Declaration() {
    override val dataBinding by DataBinding
            .create(CommonProps.dataBinding) {
                bool("isCircular")
                value("timeSpan", fallback = 3000.0f)
                enum("orientation", mapOf(
                        "vertical" to Orientation.VERTICAL,
                        "horizontal" to Orientation.HORIZONTAL
                ))
                bool("indicatorEnable")
                value("indicatorHeight", fallback = 5.0f)
                text("indicatorSelected")
                text("indicatorUnselected")
            }
}