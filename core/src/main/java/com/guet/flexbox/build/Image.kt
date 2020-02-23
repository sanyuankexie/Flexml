package com.guet.flexbox.build

object Image : Declaration() {
    override val dataBinding by DataBinding
            .create(Graphic.dataBinding) {
                value("blurRadius")
                value("blurSampling")
            }
}