package com.guet.flexbox.build.event

import android.view.View
import com.guet.flexbox.EventHandler
import com.guet.flexbox.HostContext
import com.guet.flexbox.build.execute
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.scope

internal class OnClickHandler(
        elContext: ELContext,
        hostContext: HostContext,
        private val executable: LambdaExpression
) : EventHandler(elContext, hostContext) {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        elContext.scope(mapOf(
                "pageContext" to hostContext.createPageContext(v!!)
        )) {
            executable.execute(this)
        }
    }
}