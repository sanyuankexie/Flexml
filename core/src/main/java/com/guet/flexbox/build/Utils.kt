@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import androidx.annotation.ColorInt
import com.guet.flexbox.WidgetInfo
import com.guet.flexbox.el.ELException
import lite.beans.Introspector
import java.io.*
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

internal fun Int.toColorString(): String {
    return "#" + this.toString(16)
}

private typealias FromJson<T> = (T) -> Map<String, Any>

internal object GsonMirror {
    private val calls: Map<Class<*>, FromJson<*>>

    init {
        calls = try {
            val gsonType = Class.forName("com.google.gson.Gson")
            val gson = gsonType.newInstance()
            val readerMethod = gsonType.getMethod(
                    "fromJson",
                    Reader::class.java,
                    Class::class.java
            )
            val stringMethod = gsonType.getMethod(
                    "fromJson",
                    String::class.java,
                    Class::class.java
            )
            val map = HashMap<Class<*>, FromJson<*>>()
            map.add<Reader> {
                @Suppress("UNCHECKED_CAST")
                readerMethod.invoke(gson, it, Map::class.java)
                        as Map<String, Any>
            }
            map.add<InputStream> {
                @Suppress("UNCHECKED_CAST")
                readerMethod.invoke(gson, InputStreamReader(it), Map::class.java)
                        as Map<String, Any>
            }
            map.add<ByteArray> {
                @Suppress("UNCHECKED_CAST")
                readerMethod.invoke(gson, ByteArrayInputStream(it), Map::class.java)
                        as Map<String, Any>
            }
            map.add<File> {
                @Suppress("UNCHECKED_CAST")
                readerMethod.invoke(gson, FileInputStream(it), Map::class.java)
                        as Map<String, Any>
            }
            map.add<String> {
                @Suppress("UNCHECKED_CAST")
                stringMethod.invoke(gson, it, Map::class.java)
                        as Map<String, Any>
            }
            map
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    internal fun fromJson(data: Any): Map<String, Any>? {
        return calls[data::class.java]?.let {
            @Suppress("UNCHECKED_CAST")
            return@let (it as FromJson<Any>).invoke(data)
        }
    }

    private inline fun <reified T> HashMap<Class<*>, FromJson<*>>.add(noinline action: FromJson<T>) {
        this[T::class.java] = action
    }
}

internal fun tryToMap(o: Any): Map<String, Any> {
    return if (o is Map<*, *> && o.keys.all { it is String }) {
        @Suppress("UNCHECKED_CAST")
        return o as Map<String, Any>
    } else {
        GsonMirror.fromJson(o) ?: if (o.javaClass.declaredMethods.isEmpty()) {
            o.javaClass.declaredFields.map {
                it.name to it[o]
            }.toMap()
        } else {
            Introspector.getBeanInfo(o.javaClass)
                    .propertyDescriptors
                    .filter {
                        it.propertyType != Class::class.java
                    }.map {
                        it.name to it.readMethod.invoke(o)
                    }.toMap()
        }
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

    internal fun build(): WidgetInfo {
        return WidgetInfo(type,
                Collections.unmodifiableMap(attrs),
                Collections.unmodifiableList(children.map { it.build() })
        )
    }
}

fun rootWidget(name: String, action: Builder.() -> Unit = {}): WidgetInfo {
    return Builder(name).apply(action).build()
}