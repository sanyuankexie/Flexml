package com.guet.flexbox.transaction

import com.guet.flexbox.event.ActionKey

interface TransactionExecutor {
    fun execute(
            key: ActionKey,
            args: Array<out Any?>? = null
    )
}