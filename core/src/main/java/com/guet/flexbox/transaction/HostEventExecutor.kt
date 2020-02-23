package com.guet.flexbox.transaction

import android.view.View
import com.guet.flexbox.event.ActionKey
import com.guet.flexbox.event.ActionTarget
import org.apache.commons.jexl3.JexlContext

internal class HostEventExecutor(
        private val target: ActionTarget,
        private val scope: View?,
        dataContext: JexlContext
) : ScriptExecutor(dataContext) {

    override fun execute(
            key: ActionKey,
            args: Array<out Any?>?
    ) {
        when (key) {
            ActionKey.SendObjects,
            ActionKey.RefreshPage,
            ActionKey.HttpRequest -> {
                target.dispatchAction(
                        key,
                        scope,
                        args
                )
            }
            ActionKey.ExecuteActions -> {
                super.execute(key, args)
            }
        }
    }
}