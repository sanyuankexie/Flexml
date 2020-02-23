package com.guet.flexbox.build

import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression

object TextInput : Declaration(AbsText) {
    val ATTRIBUTE_SET: DataBinding by create {
        event("onTextChanged") { pageContext: PageContext, elContext: ELContext, raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                return@let LambdaHandler(pageContext, executable)
            }
        }
    }
}