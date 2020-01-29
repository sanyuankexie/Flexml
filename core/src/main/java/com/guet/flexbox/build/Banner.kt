package com.guet.flexbox.build

import android.graphics.Color
import com.guet.flexbox.enums.Orientation

object Banner : Declaration(Common) {
    override val attributeInfoSet: AttributeInfoSet by create {
        bool("isCircular")
        value("timeSpan", fallback = 3000.0)
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
        bool("indicatorEnable")
        value("indicatorHeight", fallback = 5.0)
        color("indicatorSelectedColor", fallback = Color.WHITE)
        color("indicatorUnselectedColor", fallback = Color.GRAY)
    }
}