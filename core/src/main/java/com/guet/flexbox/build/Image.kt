package com.guet.flexbox.build

object Image : Declaration(Graphic) {
    override val attributeInfoSet: AttributeInfoSet by create {
        value("blurRadius")
        value("blurSampling")
    }
}