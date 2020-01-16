package com.guet.flexbox.transaction

import com.guet.flexbox.el.LambdaExpression

abstract class RefreshTransaction : PageTransaction() {

    protected val actions = ArrayList<LambdaExpression>()

    fun with(l: LambdaExpression): RefreshTransaction {
        actions.add(l)
        return this
    }
}