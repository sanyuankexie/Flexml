package com.guet.flexbox.build.event

import android.view.View
import com.guet.flexbox.EventHandler
import com.guet.flexbox.EventContext
import com.guet.flexbox.build.execute
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope

internal class OnClickHandler(
        elContext: ELContext,
        eventContext: EventContext,
        private val executable: LambdaExpression
) : EventHandler(elContext, eventContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        elContext.scope(mapOf(
                "pageContext" to eventContext.toPageContext(v!!)
        )) {
            executable.execute(this)
        }
    }
}