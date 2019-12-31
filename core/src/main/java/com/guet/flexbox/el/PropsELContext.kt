package com.guet.flexbox.el

import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import com.guet.flexbox.BuildConfig
import org.json.JSONArray
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

class PropsELContext(
        data: Any?
) : ELContext() {

    private val variableMapper = StandardVariableMapper()
    private val functionMapper = StandardFunctionMapper()
    private val standardResolver = CompositeELResolver()

    init {
        createPropELResolver(data)?.let {
            standardResolver.add(it)
        }
        expressionFactory.streamELResolver?.let {
            standardResolver.add(it)
        }
        standardResolver.add(staticField)
        standardResolver.add(map)
        standardResolver.add(resources)
        standardResolver.add(list)
        standardResolver.add(array)
        standardResolver.add(bean)
        standardResolver.add(jsonObject)
        standardResolver.add(jsonArray)
    }

    private fun createPropELResolver(data: Any?): ELResolver? {
        return when {
            data is Map<*, *> && data.keys.all { it is String } -> {
                PropsELResolver(data, map)
            }
            data is JSONArray -> {
                PropsELResolver(data, jsonObject)
            }
            data != null -> {
                PropsELResolver(data, bean)
            }
            else -> {
                return null
            }
        }
    }

    override fun getELResolver(): ELResolver = standardResolver

    override fun getFunctionMapper(): FunctionMapper = functionMapper

    override fun getVariableMapper(): VariableMapper = variableMapper

    @Throws(ELException::class)
    fun getValue(expr: String, type: Class<*>): Any {
        return expressionFactory
                .createValueExpression(
                        this,
                        expr,
                        type
                ).getValue(this)
                ?: throw ELException()
    }

    @Throws(ELException::class)
    internal inline fun <reified T> getValue(expr: String): T {
        return getValue(expr, T::class.java) as T
    }

    @ColorInt
    @Throws(ELException::class)
    fun getColor(expr: String): Int {
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

    internal inline fun <reified T> tryGetValue(expr: String?, fallback: T? = null): T? {
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
    internal fun tryGetColor(expr: String?, @ColorInt fallback: Int?): Int? {
        if (expr == null) {
            return fallback
        }
        return try {
            getColor(expr)
        } catch (e: ELException) {
            fallback
        }
    }

    private class StandardVariableMapper : VariableMapper() {

        private lateinit var vars: MutableMap<String, ValueExpression>

        override fun resolveVariable(variable: String): ValueExpression? {
            return if (!this::vars.isInitialized) {
                null
            } else vars[variable]
        }

        override fun setVariable(variable: String,
                                 expression: ValueExpression?
        ): ValueExpression? {
            if (!this::vars.isInitialized)
                vars = HashMap()
            return if (expression == null) {
                vars.remove(variable)
            } else {
                vars.put(variable, expression)
            }
        }
    }

    private class StandardFunctionMapper : FunctionMapper() {

        private val methods = HashMap<String, Method>(functions)

        override fun resolveFunction(
                prefix: String,
                localName: String
        ): Method? {
            val key = "$prefix:$localName"
            return methods[key]
        }

        override fun mapFunction(
                prefix: String,
                localName: String,
                method: Method?
        ) {
            val key = "$prefix:$localName"
            if (method == null) {
                methods.remove(key)
            } else {
                methods[key] = method
            }
        }
    }

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    private annotation class Prefix(val value: String)

    private companion object {

        private val array = ArrayELResolver(true)
        private val bean = BeanELResolver(true)
        private val map = MapELResolver(true)
        private val list = ListELResolver(true)
        private val jsonObject = JSONObjectELResolver(true)
        private val jsonArray = JSONArrayELResolver(true)
        private val staticField = StaticFieldELResolver()
        private val resources = ResourceBundleELResolver()

        private val expressionFactory = ELManager.getExpressionFactory()

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
                    it.apply {
                        it.isAccessible = true
                    }.let { m ->
                        "${m.getAnnotation(Prefix::class.java).value}:${m.name}" to m
                    }
                }.toMap()
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
                    }.build()
                    .toString()
        }

        @Prefix("res")
        @JvmName("drawable")
        @JvmStatic
        fun load(name: String): String {
            return Uri.Builder()
                    .scheme("res")
                    .authority("drawable")
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
