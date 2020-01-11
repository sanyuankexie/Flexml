package com.guet.flexbox.build

object Banner : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        bool("isCircular")
        value("timeSpan")
    }
}