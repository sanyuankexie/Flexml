package com.guet.flexbox.transaction

import com.guet.flexbox.el.LambdaExpression

interface RefreshTransaction : SendTransaction {
    fun with(l: LambdaExpression): RefreshTransaction

    override fun send(vararg values: Any?): RefreshTransaction
}