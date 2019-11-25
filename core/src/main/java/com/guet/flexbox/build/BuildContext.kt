package com.guet.flexbox.build

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.parseColor
import android.net.Uri
import androidx.annotation.ColorInt
import com.guet.flexbox.el.*
import java.lang.reflect.Modifier
import java.util.*

class BuildContext(data: Any?) {

    private val el = ELManager()

    init {
        el.addELResolver(JSONArrayELResolver)
        el.addELResolver(JSONObjectELResolver)
        functions.forEach { el.mapFunction(it.first, it.second.name, it.second) }
        attach(data)
    }

    private fun attach(input: Any?) {
        when {
            input == null -> {
                return
            }
            input is Map<*, *> && input.keys.all { it is String } -> {
                @Suppress("UNCHECKED_CAST")
                el.addBeanNameResolver(MapWrapper(input as MutableMap<String, Any?>))
            }
            else -> {
                el.addBeanNameResolver(ObjWrapper(input))
            }
        }
    }

    internal fun enterScope(scope: Map<String, Any?>) {
        el.elContext.enterLambdaScope(scope)
    }

    internal fun exitScope() {
        el.elContext.exitLambdaScope()
    }

    @Throws(ELException::class)
    internal fun getValue(expr: String, type: Class<*>): Any {
        return ELManager.getExpressionFactory()
                .createValueExpression(
                        el.elContext,
                        expr,
                        type
                ).getValue(el.elContext)
                ?: throw ELException()
    }

    @Throws(ELException::class)
    internal inline fun <reified T : Any> getValue(expr: String): T {
        return getValue(expr, T::class.java) as T
    }

    @ColorInt
    @Throws(ELException::class)
    internal fun getColor(expr: String): Int {
        return try {
            parseColor(expr)
        } catch (e: IllegalArgumentException) {
            scope(colorMap) {
                val value = getValue<Any>(expr)
                if (value is Number) {
                    value.toInt()
                } else {
                    parseColor(value.toString())
                }
            }
        }
    }

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Prefix(val value: String)

    companion object {

        @Suppress("UNCHECKED_CAST")
        internal val colorMap = Collections.unmodifiableMap((Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>))

        internal val functions = Functions::class.java.declaredMethods
                .filter {
                    it.modifiers.let { mod ->
                        Modifier.isPublic(mod) && Modifier.isStatic(mod)
                    } && it.isAnnotationPresent(Prefix::class.java)
                }.map {
                    it.apply { it.isAccessible = true }
                }.map {
                    it.getAnnotation(Prefix::class.java).value to it
                }.toTypedArray()
    }

    private object Functions {

        @Prefix("utils")
        @JvmName("check")
        @JvmStatic
        fun check(o: Any?): Boolean {
            return when (o) {
                is String -> o.isNotEmpty()
                is Collection<*> -> !o.isEmpty()
                is Number -> o.toInt() != 0
                else -> o != null
            }
        }

        @Prefix("res")
        @JvmName("gradient")
        @JvmStatic
        fun gradient(orientation: String, vararg colors: String): String {
            return Uri.Builder()
                    .scheme("res")
                    .authority("gradient")
                    .appendQueryParameter("orientation", orientation)
                    .apply {
                        colors.forEach {
                            appendQueryParameter("color", it)
                        }
                    }
                    .build()
                    .toString()
        }

        @Prefix("res")
        @JvmName("load")
        @JvmStatic
        fun load(name: String): String {
            return Uri.Builder()
                    .scheme("res")
                    .authority("load")
                    .appendQueryParameter("name", name)
                    .build()
                    .toString()
        }

        @Prefix("dimen")
        @JvmName("px")
        @JvmStatic
        fun px(value: Number): Double {
            return value.toDouble() / Resources.getSystem().displayMetrics.widthPixels / 360.0
        }

        @Prefix("dimen")
        @JvmName("sp")
        @JvmStatic
        fun sp(value: Number): Double {
            return (px(value) * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
        }

        @Prefix("dimen")
        @JvmName("dp")
        @JvmStatic
        fun dp(value: Number): Double {
            return (px(value) * Resources.getSystem().displayMetrics.density + 0.5f)
        }
    }
}