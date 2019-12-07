package com.luke.skywalker.el

import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.luke.skywalker.BuildConfig
import com.luke.skywalker.NodeInfo
import com.luke.skywalker.build.*
import org.json.JSONArray
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

class PropsELContext(data: Any?) : ELContext() {

    private val variableMapper = StandardVariableMapper()
    private val functionMapper = StandardFunctionMapper()
    private val standardResolver = CompositeELResolver()

    init {
        when {
            data is Map<*, *> && data.keys.all { it is String } -> {
                standardResolver.add(PropsELResolver(data, map))
            }
            data is JSONArray -> {
                standardResolver.add(PropsELResolver(data, jsonObject))
            }
            data != null -> {
                standardResolver.add(PropsELResolver(data, bean))
            }
        }
        expressionFactory.streamELResolver?.let { standardResolver.add(it) }
        standardResolver.add(staticField)
        standardResolver.add(map)
        standardResolver.add(resources)
        standardResolver.add(list)
        standardResolver.add(array)
        standardResolver.add(bean)
        standardResolver.add(jsonObject)
        standardResolver.add(jsonArray)
    }

    override fun getELResolver(): ELResolver = standardResolver

    override fun getFunctionMapper(): FunctionMapper = functionMapper

    override fun getVariableMapper(): VariableMapper = variableMapper

    @Throws(ELException::class)
    private fun getValue(expr: String, type: Class<*>): Any {
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
    internal fun getColor(expr: String): Int {
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

    internal inline fun <reified T> tryGetValue(expr: String?, fallback: T): T {
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

    internal inline fun <T> scope(scope: Map<String, Any>, action: () -> T): T {
        enterLambdaScope(scope)
        try {
            return action()
        } finally {
            exitLambdaScope()
        }
    }

    internal inline fun <reified T : Enum<T>> tryGetEnum(
            expr: String?,
            scope: Map<String, T>,
            fallback: T = enumValues<T>().first()): T {
        return when {
            expr == null -> fallback
            expr.isExpr -> scope(scope) {
                tryGetValue(expr, fallback)
            }
            else -> scope[expr] ?: fallback
        }
    }

    internal fun tryGetLambda(expr: String?): LambdaExpression? {
        if (expr == null) {
            return null
        }
        @Suppress("RemoveExplicitTypeArguments")
        return tryGetValue<LambdaExpression?>(if (!expr.isExpr) {
            "\${$expr}"
        } else {
            expr
        }, null)
    }

    internal inline fun <reified T : Any> requestValue(
            name: String,
            attrs: Map<String, String>
    ): T {
        return getValue(attrs[name] ?: error("request attr '$name'"))
    }

    @ColorInt
    internal fun tryGetColor(expr: String?, @ColorInt fallback: Int): Int {
        if (expr == null) {
            return fallback
        }
        return try {
            getColor(expr)
        } catch (e: ELException) {
            fallback
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun inflate(
            c: ComponentContext,
            element: NodeInfo
    ): Component? {
        return inflate(c, element, View.VISIBLE).singleOrNull()
    }

    internal fun inflate(
            c: ComponentContext,
            element: NodeInfo,
            upperVisibility: Int
    ): List<Component> {
        return standardTransforms[element.type]?.transform(
                c,
                this,
                element,
                upperVisibility
        ) ?: emptyList()
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


        private val expressionFactory = ExpressionFactory.newInstance()

        private val standardTransforms = mapOf(
                "Image" to ImageFactory,
                "Flex" to FlexFactory,
                "Text" to TextFactory,
                "Stack" to StackFactory,
                "Native" to NativeFactory,
                "TextInput" to TextInputFactory,
                "Scroller" to ScrollerFactory,
                "Empty" to EmptyFactory,
                "for" to ForBehavior,
                "foreach" to ForEachBehavior,
                "if" to IfBehavior
        )

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

        @Prefix("utils")
        @JvmName("arrayOf")
        @JvmStatic
        fun arrayOf(vararg value: Any): Array<Any> {
            return kotlin.arrayOf(value)
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
