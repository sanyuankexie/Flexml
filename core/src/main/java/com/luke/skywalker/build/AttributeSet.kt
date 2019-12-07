package com.luke.skywalker.build

import android.graphics.Color
import com.facebook.litho.Component

internal class AttributeSet<T : Component.Builder<*>> {

    val values = HashMap<String, Mapping<T>>()

    inline fun <reified V : Any> scopeAttr(
            name: String,
            scope: Map<String, V>,
            fallback: V,
            crossinline action: Apply<T, V>
    ) {
        values[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.scope(scope) {
                    c.tryGetValue(value, fallback)
                }
            } else {
                scope[value] ?: fallback
            })
        }
    }

    inline fun <reified V : Enum<V>> enumAttr(
            name: String,
            scope: Map<String, V>,
            fallback: V = enumValues<V>().first(),
            crossinline action: Apply<T, V>
    ) {
        scopeAttr(name, scope, fallback, action)
    }

    inline fun textAttr(
            name: String,
            fallback: String = "",
            crossinline action: Apply<T, String>) {
        values[name] = { c, map, display, value ->
            action(map, display, c.tryGetValue(value, fallback))
        }
    }

    inline fun boolAttr(
            name: String,
            fallback: Boolean = false,
            crossinline action: Apply<T, Boolean>) {
        values[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.tryGetValue(value, fallback)
            } else {
                try {
                    value.toBoolean()
                } catch (e: Exception) {
                    fallback
                }
            })
        }
    }

    inline fun <reified N : Number> numberAttr(
            name: String,
            crossinline action: Apply<T, N>) {
        values[name] = { c, map, display, value ->
            action(map, display, c.tryGetValue(value, c.getValue("0")))
        }
    }

    inline fun <reified N : Number> numberAttr(
            name: String,
            fallback: N,
            crossinline action: Apply<T, N>) {
        values[name] = { c, map, display, value ->
            action(map, display, c.tryGetValue(value, fallback))
        }
    }

    inline fun colorAttr(
            name: String,
            fallback: Int = Color.TRANSPARENT,
            crossinline action: Apply<T, Int>) {
        values[name] = { c, map, display, value ->
            action(map, display, if (value.isExpr) {
                c.tryGetColor(value, fallback)
            } else {
                try {
                    Color.parseColor(value)
                } catch (e: Exception) {
                    fallback
                }
            })
        }
    }
}