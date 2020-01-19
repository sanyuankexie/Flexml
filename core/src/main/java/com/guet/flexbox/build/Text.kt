package com.guet.flexbox.build

object Text : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        text("text")
        bool("clipToBounds")
        color("textColor")
    }
}