package com.guet.flexbox.event

import com.facebook.litho.ClickEvent
import com.guet.flexbox.JoinPageContext
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope
import java.util.*

class OnClickEventHandler(
        private val pageContext: PageContext,
        private val elContext: ELContext,
        private val executable: LambdaExpression
) : EventHandler<ClickEvent>() {
    override fun dispatchEvent(event: ClickEvent) {
        elContext.scope(Collections.singletonMap(
                "pageContext", JoinPageContext(pageContext, event.view)
        )) {
            executable.invoke(elContext)
        }
    }
}