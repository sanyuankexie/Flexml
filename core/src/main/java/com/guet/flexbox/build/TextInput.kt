package com.guet.flexbox.build

import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.litho.LithoEventHandler
import com.guet.flexbox.withView
import java.util.*

object TextInput : Declaration(AbstractText) {
    override val attributeInfoSet: AttributeInfoSet by create {
        typed("onTextChanged") { pageContext: PageContext,
                                 elContext: PropsELContext,
                                 raw: String ->
            elContext.tryGetValue<LambdaExpression>(raw)?.let { executable ->
                LithoEventHandler.create<TextChangedEvent> { event ->
                    elContext.scope(Collections.singletonMap(
                            "pageContext", pageContext.withView(event.view)
                    )) {
                        executable.exec(elContext, event.text)
                    }
                }
            }
        }
    }
}