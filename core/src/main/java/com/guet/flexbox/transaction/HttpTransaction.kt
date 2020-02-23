package com.guet.flexbox.transaction

import com.guet.flexbox.el.LambdaExpression

interface HttpTransaction : SendTransaction {

    fun url(url: String): HttpTransaction

    fun method(method: String): HttpTransaction

    fun with(key: String, value: String): HttpTransaction

    fun error(lambdaExpression: LambdaExpression): HttpTransaction

    fun success(lambdaExpression: LambdaExpression): HttpTransaction

    override fun send(vararg values: Any?): HttpTransaction
}