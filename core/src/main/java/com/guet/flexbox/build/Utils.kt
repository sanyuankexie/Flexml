@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.ColorInt
import com.guet.flexbox.el.ELException
import lite.beans.Introspector
import java.io.*
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

@Volatile
internal var metrics = Resources.getSystem().displayMetrics
    private set

internal fun init(c: Context) {
    if (Callbacks.compareAndSet(false, true)) {
        c.applicationContext.registerComponentCallbacks(Callbacks)
    }
}

private object Callbacks : AtomicBoolean(false), ComponentCallbacks {

    override fun onLowMemory() = Unit

    override fun onConfigurationChanged(newConfig: Configuration?) {
        metrics = Resources.getSystem().displayMetrics
    }
}

internal inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * metrics.widthPixels / 360f).toInt()
}

internal inline fun <reified T : Any> BuildContext.tryGetValue(expr: String?, fallback: T): T {
    if (expr == null) {
        return fallback
    }
    return try {
        getValue(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal inline fun <T> BuildContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterScope(scope)
    try {
        return action()
    } finally {
        exitScope()
    }
}

internal inline fun <reified T : Enum<T>> BuildContext.tryGetEnum(
        expr: String?,
        scope: Map<String, T>,
        fallback: T = T::class.java.enumConstants[0]): T {
    return when {
        expr == null -> fallback
        expr.isExpr -> scope(scope) {
            tryGetValue(expr, fallback)
        }
        else -> scope[expr] ?: fallback
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

internal inline val CharSequence?.isExpr: Boolean
    get() = this != null && length > 3 && startsWith("\${") && endsWith('}')

internal inline fun <reified N : Number> Number.safeCast(): N {
    return safeCast(N::class) as N
}

private fun Number.safeCast(type: KClass<*>): Any {
    return when (type) {
        Byte::class -> this.toByte()
        Char::class -> this.toChar()
        Int::class -> this.toInt()
        Short::class -> this.toShort()
        Long::class -> this.toLong()
        Float::class -> this.toFloat()
        Double::class -> this.toDouble()
        else -> error("no match number type ${type.java.name}")
    }
}

private typealias FromJson<T> = (T, Type) -> Any

private typealias TypeMatcher = HashMap<Class<*>, FromJson<*>>

private object GsonMirror {

    private val map = TypeMatcher(5)

    init {
        try {
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
            val converter: FromJson<InputStream> = { data, type ->
                readerMethod.invoke(gson, InputStreamReader(data), type)
            }
            map.add<Reader> { data, type ->
                readerMethod.invoke(gson, data, type)
            }
            map.add(converter)
            map.add<ByteArray> { data, type ->
                converter(ByteArrayInputStream(data), type)
            }
            map.add<File> { data, type ->
                converter(FileInputStream(data), type)
            }
            map.add<String> { data, type ->
                stringMethod.invoke(gson, data, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inline fun <reified T> fromJson(data: Any): T? {
        return map[T::class.java]?.let {
            @Suppress("UNCHECKED_CAST")
            (it as FromJson<Any>).invoke(data, T::class.java)
        } as? T
    }

    private inline fun <reified T> TypeMatcher.add(noinline action: FromJson<T>) {
        this[T::class.java] = action
    }
}

internal fun tryToMap(o: Any): Map<String, Any> {
    return if (o is Map<*, *> && o.keys.all { it is String }) {
        @Suppress("UNCHECKED_CAST")
        return o as Map<String, Any>
    } else {
        @Suppress("UNCHECKED_CAST")
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