package com.guet.flexbox.build

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.annotation.ColorInt
import com.guet.flexbox.el.ELException
import com.guet.flexbox.el.ELManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class DataBinding private constructor(data: Any?) : ELManager() {

    init {
        addELResolver(JsonELResolver)
        if (data != null) {
            enterScope(tryToMap(data))
        }
        functions.forEach {
            mapFunction(it.first, it.second.name, it.second)
        }
    }

    internal fun enterScope(scope: Map<String, Any>) {
        elContext.enterLambdaScope(scope)
    }

    internal fun exitScope() {
        elContext.exitLambdaScope()
    }

    @Throws(ELException::class)
    internal fun getValue(expr: String, type: Class<*>): Any {
        return ELManager.getExpressionFactory()
                .createValueExpression(
                        elContext,
                        expr,
                        type
                ).getValue(elContext)
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

        @JvmStatic
        fun create(data: Any?): DataBinding {
            return DataBinding(data)
        }

        @Suppress("UNCHECKED_CAST")
        internal val colorMap = HashMap((Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>))

        internal val functions: Array<Pair<String, Method>> = Functions::class.java.declaredMethods
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

    internal object Functions {

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

        @Prefix("draw")
        @JvmName("gradient")
        @JvmStatic
        fun gradient(
                orientation: Orientation,
                vararg colors: String
        ): GradientDrawable {
            return GradientDrawable(orientation, colors.map {
                parseColor(it)
            }.toIntArray())
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