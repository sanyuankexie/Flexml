@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import androidx.annotation.ColorInt
import com.guet.flexbox.WidgetInfo
import com.guet.flexbox.el.ELException
import java.util.*
import kotlin.collections.HashMap

internal fun Long.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360).toInt()
}

internal fun Double.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360).toInt()
}

internal fun Short.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360)
}

internal fun Int.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360)
}

internal fun Float.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360).toInt()
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

class Builder internal constructor(private val type: String) {
    private val attrs = HashMap<String, String>()
    private val children = LinkedList<Builder>()

    fun widget(name: String, action: Builder.() -> Unit = {}) {
        children.add(Builder(name).apply(action))
    }

    fun attr(name: String, action: () -> Any = { "" }) {
        attrs[name] = action().toString()
    }

    fun build(): WidgetInfo {
        return WidgetInfo(type, attrs, children.map { it.build() })
    }
}

fun rootWidget(name: String, action: Builder.() -> Unit = {}): WidgetInfo {
    return Builder(name).apply(action).build()
}