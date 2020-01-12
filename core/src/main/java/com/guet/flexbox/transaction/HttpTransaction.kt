package com.guet.flexbox.transaction

import android.util.ArrayMap
import com.guet.flexbox.el.LambdaExpression

abstract class HttpTransaction : PageTransaction() {

    protected var url: String? = null
    protected var method: String? = null
    protected val prams = ArrayMap<String, String>()
    protected var error: LambdaExpression? = null
    protected var success: LambdaExpression? = null

    fun url(url: String): HttpTransaction {
        this.url = url
        return this
    }

    fun method(method: String): HttpTransaction {
        this.method = method
        return this
    }

    fun pram(key: String, value: String): HttpTransaction {
        prams[key] = value
        return this
    }

    fun error(lambdaExpression: LambdaExpression): HttpTransaction {
        error = lambdaExpression
        return this
    }

    fun success(lambdaExpression: LambdaExpression): HttpTransaction {
        success = lambdaExpression
        return this
    }
}