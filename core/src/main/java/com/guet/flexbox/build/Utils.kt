package com.guet.flexbox.build

typealias PropSet = Map<String, Any>

inline val String.isExpr: Boolean
    get() {
        val trim = trim()
        return trim.startsWith("\${") && trim.endsWith("}")
    }

inline val String.innerExpr: String
    get() {
        return trim().substring(2, length - 1)
    }


