package com.guet.flexbox.build

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.parseColor
import android.net.Uri
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.BuildConfig
import com.guet.flexbox.EventListener
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.*
import java.lang.reflect.Modifier
import java.util.*

internal class PagerContext(
        data: Any?,
        eventListener: EventListener?
) {

    private val el = ELManager()

    init {
        el.addELResolver(JSONArrayELResolver)
        el.addELResolver(JSONObjectELResolver)
        functions.forEach { el.mapFunction(it.first, it.second.name, it.second) }
        el.defineBean("eventBus", EventBus(eventListener))
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

    private fun enterScope(scope: Map<String, Any?>) {
        el.elContext.enterLambdaScope(scope)
    }

    private fun exitScope() {
        el.elContext.exitLambdaScope()
    }

    @Throws(ELException::class)
    private fun getValue(expr: String, type: Class<*>): Any {
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

    internal inline fun <reified T : Any> tryGetValue(expr: String?, fallback: T): T {
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
        enterScope(scope)
        try {
            return action()
        } finally {
            exitScope()
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

    @JvmOverloads
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun inflate(
            c: ComponentContext,
            element: NodeInfo,
            upperVisibility: Int = View.VISIBLE
    ): List<Component> {
        return standardTransforms[element.type]?.transform(
                c,
                this,
                element,
                upperVisibility
        ) ?: emptyList()
    }

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Prefix(val value: String)

    internal companion object {

        private val standardTransforms = mapOf(
                "Image" to ImageFactory,
                "Flex" to FlexFactory,
                "Text" to TextFactory,
                "Stack" to StackFactory,
                "Native" to NativeFactory,
                "Scroller" to ScrollerFactory,
                "Empty" to EmptyFactory,
                "TextInput" to TextInputFactory,
                "root" to RootTransform,
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