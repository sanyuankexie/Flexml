package com.guet.flexbox.transaction

interface Dispatcher {
    fun dispatchActions(
            key: ActionKey,
            args: Array<out Any?>? = null
    )
}