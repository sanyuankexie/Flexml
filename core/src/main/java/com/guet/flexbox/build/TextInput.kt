package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.build.event.OnTextChangedHandler
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.tryGetValue

object TextInput : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        event("onTextChanged") { hostContext: HostContext, elContext: ELContext, raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                return@let OnTextChangedHandler(elContext, hostContext, executable)
            }
        }
    }
}