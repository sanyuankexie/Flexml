package com.guet.flexbox.build

object Text : Declaration() {
    override val dataBinding by DataBinding.create(AbsText.dataBinding) {
        text("text")
        bool("clipToBounds")
        color("textColor")
    }
}