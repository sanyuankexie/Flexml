package com.guet.flexbox.databinding

import com.guet.flexbox.widget.Orientation

internal object Scroller : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        bool("scrollBarEnable")
        enum("orientation", mapOf(
                "vertical" to Orientation.VERTICAL,
                "horizontal" to Orientation.HORIZONTAL
        ))
    }
}