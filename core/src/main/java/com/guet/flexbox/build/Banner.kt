package com.guet.flexbox.build

object Banner : Declaration(Common) {
    override val attributeInfoSet: AttributeInfoSet by create {
        bool("isCircular")
        value("timeSpan")
    }
}