package com.guet.flexbox.eventsystem

import android.view.View
import com.guet.flexbox.build.DataBinder
import com.guet.flexbox.build.innerExpr
import com.guet.flexbox.build.isExpr
import com.guet.flexbox.eventsystem.event.ClickUrlEvent
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

internal class ClickUrlEventReceiver(
        private val eventDispatcher: EventTarget,
        private val url: String
) : ExternalEventReceiver {

    override fun receive(v: View?, args: Array<out Any?>?) {
        eventDispatcher.dispatchEvent(
                ClickUrlEvent(v!!, url)
        )
    }

    companion object Covertor : DataBinder<ClickUrlEventReceiver> {
        override fun cast(
                engine: JexlEngine,
                dataContext: JexlContext,
                eventDispatcher: EventTarget,
                raw: String
        ): ClickUrlEventReceiver? {
            val url = if (raw.isExpr) {
                engine.createExpression(raw.innerExpr)
                        .evaluate(dataContext) as? String ?: ""
            } else {
                raw
            }
            return ClickUrlEventReceiver(eventDispatcher, url)
        }
    }
}