@file:JvmName("-Utils")

package com.guet.flexbox.build

import android.content.res.Resources
import android.view.View
import androidx.annotation.ColorInt
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.BuildConfig
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.beans.Introspector
import com.guet.flexbox.beans.PropertyDescriptor
import com.guet.flexbox.el.ELException
import org.json.JSONObject
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal typealias Mapping<T> = T.(DataContext, Map<String, String>, Boolean, String) -> Unit

internal typealias Mappings<T> = HashMap<String, Mapping<T>>

internal typealias Apply<T, V> = T.(Map<String, String>, Boolean, V) -> Unit

private class SimpleBeanMap(private val o: Any) : AbstractMap<String, Any?>() {

    override val entries: Set<Map.Entry<String, Any?>> by lazy {
        o.javaClass.fields.filter {
            !Modifier.isStatic(it.modifiers)
        }.map {
            Property(it)
        }.toSet()
    }

    private inner class Property(private val it: Field)
        : Map.Entry<String, Any?> {
        override val key: String
            get() = it.name
        override val value: Any?
            get() = it.get(o)
    }

}

private class StandardBeanMap(private val o: Any) : AbstractMap<String, Any?>() {

    override val entries: Set<Map.Entry<String, Any?>> by lazy {
        Introspector.getBeanInfo(javaClass)
                .propertyDescriptors
                .filter {
                    it.propertyType != Class::class.java
                }.map {
                    Property(it)
                }.toSet()
    }

    private inner class Property(private val prop: PropertyDescriptor)
        : Map.Entry<String, Any?> {
        override val key: String
            get() = prop.name
        override val value: Any?
            get() = prop.readMethod.invoke(o)
    }

}

private val transforms = mapOf(
        "Image" to ImageFactory,
        "Flex" to FlexFactory,
        "Text" to TextFactory,
        "Frame" to FrameFactory,
        "Native" to NativeFactory,
        "Scroller" to ScrollerFactory,
        "Empty" to EmptyFactory,
        "for" to ForBehavior,
        "foreach" to ForEachBehavior,
        "if" to IfBehavior
)

private val jsonObjectInnerMap = JSONObject::class.java
        .getField("nameValuePairs")
        .apply { isAccessible = true }

internal inline val CharSequence.isExpr: Boolean
    @JvmName("isExprNonNull")
    get() = length > 3 && startsWith("\${") && endsWith('}')

internal inline fun <reified T : Number> T.toPx(): Int {
    return (this.toFloat() * Resources.getSystem().displayMetrics.widthPixels / 360f).toInt()
}

internal inline fun <reified T : Any> DataContext.tryGetValue(expr: String?, fallback: T): T {
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

internal inline fun <T> DataContext.scope(scope: Map<String, Any>, action: () -> T): T {
    enterScope(scope)
    try {
        return action()
    } finally {
        exitScope()
    }
}

internal inline fun <reified T : Enum<T>> DataContext.tryGetEnum(
        expr: String?,
        scope: Map<String, T>,
        fallback: T = enumValues<T>()[0]): T {
    return when {
        expr == null -> fallback
        expr.isExpr -> scope(scope) {
            tryGetValue(expr, fallback)
        }
        else -> scope[expr] ?: fallback
    }
}

internal inline fun <reified T : Any> DataContext.requestValue(
        name: String,
        attrs: Map<String, String>
): T {
    return getValue(attrs[name] ?: error("request attr '$name'"))
}

@ColorInt
internal fun DataContext.tryGetColor(expr: String?, @ColorInt fallback: Int): Int {
    if (expr == null) {
        return fallback
    }
    return try {
        getColor(expr)
    } catch (e: ELException) {
        fallback
    }
}

internal inline fun <reified N : Number> Number.safeCast(): N {
    return safeCast(N::class.javaObjectType) as N
}

private fun Number.safeCast(type: Class<*>): Any {
    return when (type) {
        Byte::class.javaObjectType -> this.toByte()
        Char::class.javaObjectType -> this.toChar()
        Int::class.javaObjectType -> this.toInt()
        Short::class.javaObjectType -> this.toShort()
        Long::class.javaObjectType -> this.toLong()
        Float::class.javaObjectType -> this.toFloat()
        Double::class.javaObjectType -> this.toDouble()
        else -> error("no match number type ${type.name}")
    }
}

internal fun tryToMap(input: Any): Map<String, Any?> {
    val javaClass = input.javaClass
    @Suppress("UNCHECKED_CAST")
    return when {
        input is Map<*, *> && input.keys.all { it is String } -> {
            input
        }
        input is JSONObject -> {
            jsonObjectInnerMap.get(input)
        }
        javaClass.methods.all { it.declaringClass == Any::class.java } -> {
            SimpleBeanMap(input)
        }
        else -> {
            StandardBeanMap(input)
        }
    } as Map<String, Any?>
}

internal fun ComponentContext.createFromElement(
        dataBinding: DataContext,
        element: NodeInfo,
        upperVisibility: Int = View.VISIBLE
): List<Component> {
    return transforms[element.type]?.transform(
            this,
            dataBinding,
            element,
            upperVisibility
    ) ?: emptyList()
}