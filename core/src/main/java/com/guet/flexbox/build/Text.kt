package com.guet.flexbox.build

object Text : Declaration(AbsText) {
    val ATTRIBUTE_RESOLVER_SET: DataBinding by create {
        text("text")
        bool("clipToBounds")
        color("textColor")
    }
}