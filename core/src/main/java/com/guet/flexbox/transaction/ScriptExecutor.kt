package com.guet.flexbox.transaction

import com.guet.flexbox.event.ActionKey
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

open class ScriptExecutor(
        private val dataContext: JexlContext
) : ActionExecutor {
    override fun execute(
            key: ActionKey,
            args: Array<out Any?>?
    ) {
        if (key == ActionKey.ExecuteActions) {
            if (!args.isNullOrEmpty()) {
                args.mapNotNull {
                    it as? JexlScript
                }.forEach {
                    it.execute(dataContext)
                }
            }
        }
    }
}