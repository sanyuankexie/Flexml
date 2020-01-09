package com.guet.flexbox.build

internal object Text : Declaration(AbstractText) {
    override val attributeSet: AttributeSet by create {
        text("text")
        bool("clipToBounds")
        color("textColor")
    }
}