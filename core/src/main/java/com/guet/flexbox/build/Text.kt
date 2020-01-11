package com.guet.flexbox.build

object Text : Declaration(AbstractText) {
    override val attributeSet: AttributeSet by create {
        text("text")
        bool("clipToBounds")
        color("textColor")
    }
}