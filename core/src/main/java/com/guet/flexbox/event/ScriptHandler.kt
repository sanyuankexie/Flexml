package com.guet.flexbox.event

import android.view.View
import com.guet.flexbox.PageContext
import com.guet.flexbox.transaction.ComposeExecutor
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

internal class ScriptHandler(
        private val pageContext: PageContext,
        private val script: JexlScript,
        private val dataContext: JexlContext
) : EventHandler {
    override fun handleEvent(v: View?, args: Array<out Any?>?) {
        script.execute(dataContext, args)
        pageContext.executeTransaction(ComposeExecutor(
                ScriptExecutor(dataContext),
                pageContext.newHostEventExecutor(v)
        ))
    }
}