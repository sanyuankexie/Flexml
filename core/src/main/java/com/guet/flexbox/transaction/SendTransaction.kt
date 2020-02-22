package com.guet.flexbox.transaction

interface SendTransaction : PageTransaction {
    fun send(vararg values: Any?): SendTransaction
}