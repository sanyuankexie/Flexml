package com.guet.flexbox

import com.guet.flexbox.el.LambdaExpression

interface HttpTransaction : PageTransaction {

    fun pram(key: String, value: String)

    fun error(lambdaExpression: LambdaExpression): HttpTransaction

    fun success(lambdaExpression: LambdaExpression): HttpTransaction

}