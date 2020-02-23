package com.guet.flexbox.transaction

import com.guet.flexbox.event.ActionKey

class ComposeExecutor(
        private vararg val executors: ActionExecutor
) : ActionExecutor {
    override fun execute(key: ActionKey, args: Array<out Any?>?) {
        executors.forEach {
            it.execute(key, args)
        }
    }
}