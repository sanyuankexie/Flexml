package com.guet.flexbox.el

internal inline fun <T> ELContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterLambdaScope(scope)
    try {
        return action()
    } finally {
        exitLambdaScope()
    }
}