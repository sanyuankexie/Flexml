package com.guet.flexbox.build

import com.guet.flexbox.build.event.LambdaHandler
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.tryGetValue
import com.guet.flexbox.transaction.PageContext

object TextInput : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        event("onTextChanged") { pageContext: PageContext, elContext: ELContext, raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                return@let LambdaHandler(pageContext, executable)
            }
        }
    }
}