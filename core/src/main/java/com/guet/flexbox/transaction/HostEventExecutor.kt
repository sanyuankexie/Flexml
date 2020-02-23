package com.guet.flexbox.transaction

import android.view.View
import com.guet.flexbox.event.ActionKey
import com.guet.flexbox.event.ActionTarget

internal class HostEventExecutor(
        private val target: ActionTarget,
        private val scope: View?
) : ActionExecutor {

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
            else -> Unit
        }
    }
}