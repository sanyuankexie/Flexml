package com.guet.flexbox.build

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.guet.flexbox.el.ELException
import org.dom4j.Attribute

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Number.toPx(): Int {
    return (this.toDouble() * Resources.getSystem().displayMetrics.widthPixels / 360).toInt()
}

internal operator fun List<Attribute>.get(name: String): String? {
    return this.filter {
        it.name == name
    }.map { it.value }.firstOrNull()
}

internal fun <T> BuildContext.getValue(expr: String?, type: Class<T>, fallback: T): T {
    if (expr == null) {
        return fallback
    }
    return try {
        getValue(expr, type)
    } catch (e: ELException) {
        fallback
    }
}

@ColorInt
internal fun BuildContext.getColor(expr: String?, @ColorInt fallback: Int): Int {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

inline fun <T> BuildContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterScope(scope)
    try {
        return action()
    } finally {
        exitScope()
    }
}
