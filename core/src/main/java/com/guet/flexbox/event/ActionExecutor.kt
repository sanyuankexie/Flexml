package com.guet.flexbox.event

interface ActionExecutor {
    fun execute(
            key: ActionKey,
            args: Array<out Any?>? = null
    )
}