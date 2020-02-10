package com.guet.flexbox.el

import android.graphics.Color
import androidx.annotation.ColorInt
import com.guet.flexbox.BuildConfig
import java.util.*

@Suppress("UNCHECKED_CAST")
internal val colorMap = Collections.unmodifiableMap(
        (Color::class.java.getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>))

internal inline fun <T> ELContext.scope(
        scope: Map<String, Any>,
        action: ELContext.() -> T
): T {
    enterLambdaScope(scope)
    try {
        return action()
    } finally {
        exitLambdaScope()
    }
}

@Throws(ELException::class)
internal inline fun <reified T> ELContext.getValue(expr: String): T {
    return (this as PropsELContext).getValue(expr, T::class.java) as T
}

@ColorInt
@Throws(ELException::class)
internal fun ELContext.getColor(expr: String): Int {
    return try {
        Color.parseColor(expr)
    } catch (e: IllegalArgumentException) {
        scope(colorMap) {
            val value = getValue<Any>(expr)
            if (value is Number) {
                value.toInt()
            } else {
                Color.parseColor(value.toString())
            }
        }
    }
}

internal inline fun <reified T> ELContext.tryGetValue(expr: String?, fallback: T? = null): T? {
    if (expr == null) {
        return fallback
    }
    return try {
        getValue(expr)
    } catch (e: ELException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        fallback
    }
}

@ColorInt
internal fun ELContext.tryGetColor(expr: String?, @ColorInt fallback: Int?): Int? {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

