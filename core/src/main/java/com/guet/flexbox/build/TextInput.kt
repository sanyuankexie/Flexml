package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue
import java.util.*

object TextInput : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        event("onTextChanged") { pageContext: HostContext,
                                 elContext: ELContext,
                                 raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                { view, args ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", pageContext.createPageContext(view!!)
                    )) {
                        executable.execute(this, args!![0])
                    }
                }
            }
        }
    }
}