package com.guet.flexbox.transaction


interface PageContext {

    fun send(vararg values: Any)

    fun http(): HttpTransaction

    fun refresh(): RefreshTransaction
}