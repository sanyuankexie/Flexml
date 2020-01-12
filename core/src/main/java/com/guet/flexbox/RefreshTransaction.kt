package com.guet.flexbox

import com.guet.flexbox.el.LambdaExpression

interface RefreshTransaction : PageTransaction {
    fun with(lambdaExpression: LambdaExpression): RefreshTransaction
}