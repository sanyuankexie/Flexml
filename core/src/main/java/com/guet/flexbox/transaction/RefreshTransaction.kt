package com.guet.flexbox.transaction

import com.guet.flexbox.el.LambdaExpression

abstract class RefreshTransaction : PageTransaction() {

    protected val runs = ArrayList<LambdaExpression>()

    fun with(lambdaExpression: LambdaExpression): RefreshTransaction {
        runs.add(lambdaExpression)
        return this
    }
}