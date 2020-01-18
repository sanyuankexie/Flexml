package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope
import java.util.*

object TextInput : Declaration(AbsText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        typed("onTextChanged") { pageContext: HostingContext,
                                 elContext: PropsELContext,
                                 raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                EventHandlerFactory.create { view, args ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", pageContext.withView(view)
                    )) {
                        executable.exec(elContext, args[0])
                    }
                }
            }
        }
    }
}