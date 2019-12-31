package com.guet.flexbox.event

import com.facebook.litho.widget.TextChangedEvent
import com.guet.flexbox.JoinPageContext
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope
import java.util.*

class OnTextChangedEventHandler(
        private val pageContext: PageContext,
        private val elContext: ELContext,
        private val executable: LambdaExpression
) : AbstractEventHandler<TextChangedEvent>() {
    override fun dispatchEvent(event: TextChangedEvent) {
        elContext.scope(Collections.singletonMap(
                "pageContext", JoinPageContext(pageContext, event.view, event.text)
        )) {
            executable.invoke(elContext, event.text)
        }
    }
}