package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue
import java.util.*

object TextInput : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        event("onTextChanged") { pageContext: HostingContext,
                                 elContext: ELContext,
                                 raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                { view, args ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", pageContext.withView(view)
                    )) {
                        executable.exec(this, args[0])
                    }
                }
            }
        }
    }
}