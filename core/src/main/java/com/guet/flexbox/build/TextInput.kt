package com.guet.flexbox.build


object TextInput : Declaration() {
    override val dataBinding by DataBinding
            .create(CommonProps.dataBinding) {
        event("onTextChanged")
    }
}