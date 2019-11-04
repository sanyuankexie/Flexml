@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import androidx.annotation.ColorInt
import com.guet.flexbox.el.ELException
import lite.beans.Introspector
import java.io.*
import java.lang.reflect.Type


internal fun Double.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360).toInt()
}

internal fun Int.toPx(): Int {
    return (this * Resources.getSystem().displayMetrics.widthPixels / 360)
}

private typealias FromJson<T> = (T, Type) -> Any

private object GsonMirror {
    private val calls: Map<Class<*>, FromJson<*>>

    init {
        calls = try {
            val gsonType = Class.forName("com.google.gson.Gson")
            val gson = gsonType.newInstance()
            val readerMethod = gsonType.getMethod(
                    "fromJson",
                    Reader::class.java,
                    Type::class.java
            )
            val stringMethod = gsonType.getMethod(
                    "fromJson",
                    String::class.java,
                    Type::class.java
            )
            val map = HashMap<Class<*>, FromJson<*>>()
            val inputStreamFunc: FromJson<InputStream> = { data, type ->
                readerMethod.invoke(gson, InputStreamReader(data), type)
            }
            map.add<Reader> { data, type ->
                readerMethod.invoke(gson, data, type)
            }
            map.add(inputStreamFunc)
            map.add<ByteArray> { data, type ->
                inputStreamFunc(ByteArrayInputStream(data), type)
            }
            map.add<File> { data, type ->
                inputStreamFunc(FileInputStream(data), type)
            }
            map.add<String> { data, type ->
                stringMethod.invoke(gson, data, type)
            }
            map
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    internal fun <T> fromJson(data: Any, type: Type): T? {
        return calls[data::class.java]?.let {
            @Suppress("UNCHECKED_CAST")
            return@let (it as FromJson<Any>).invoke(data, type) as T
        }
    }

    private inline fun <reified T> HashMap<Class<*>, FromJson<*>>.add(noinline action: FromJson<T>) {
        this[T::class.java] = action
    }
}

internal fun <T> BuildContext.tryGetValue(expr: String?, type: Class<T>, fallback: T): T {
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
internal fun BuildContext.tryGetColor(expr: String?, @ColorInt fallback: Int): Int {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal fun tryToMap(o: Any): Map<String, Any> {
    return if (o is Map<*, *> && o.keys.all { it is String }) {
        @Suppress("UNCHECKED_CAST")
        return o as Map<String, Any>
    } else {
        @Suppress("UNCHECKED_CAST")
        GsonMirror.fromJson(o, Map::class.java) ?: if (o.javaClass.declaredMethods.isEmpty()) {
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