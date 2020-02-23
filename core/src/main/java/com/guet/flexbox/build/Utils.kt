package com.guet.flexbox.build

import android.graphics.Color

typealias AttributeSet = Map<String, Any>

inline val String.isExpr: Boolean
    get() {
        val trim = trim()
        return trim.startsWith("\${") && trim.endsWith("}")
    }

inline val String.innerExpr: String
    get() {
        return trim().substring(2, length - 1)
    }

@Suppress("UNCHECKED_CAST")
internal val colorScope: Map<String, String> = (Color::class.java
        .getDeclaredField("sColorNameMap")
        .apply { isAccessible = true }
        .get(null) as Map<String, Int>).map {
    it.key to "#" + String.format("%08x", it.value)
}.toMap()

