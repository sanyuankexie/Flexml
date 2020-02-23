package com.guet.flexbox.transaction.dispatch

interface Dispatcher {
    fun dispatchAction(
            key: ActionKey,
            args: Array<out Any?>? = null
    )
}