package com.guet.flexbox.databinding

internal object Text : Declaration(AbstractText) {
    override val attributeSet: AttributeSet by create {
        text("text")
        bool("clipToBounds")
        color("textColor")
        value("textSize", fallback = 13.0)
    }
}