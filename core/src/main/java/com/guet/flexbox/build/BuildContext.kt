package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.NodeInfo
import com.guet.flexbox.el.ELException
import com.guet.flexbox.el.ELManager
import com.guet.flexbox.el.ELProcessor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class BuildContext(val componentContext: ComponentContext, data: Any?) {

    private val el = ELProcessor()

    init {
        if (data != null) {
            enterScope(tryToMap(data))
        }
        functions.forEach {
            el.defineFunction(it.getAnnotation(Prefix::class.java).value, it.name, it)
        }
    }

    internal fun enterScope(scope: Map<String, Any>) {
        el.elManager.elContext.enterLambdaScope(scope)
    }

    internal fun exitScope() {
        el.elManager.elContext.exitLambdaScope();
    }

    @Throws(ELException::class)
    fun <T> getValue(expr: String, type: Class<T>): T {
        val ve = ELManager.getExpressionFactory()
                .createValueExpression(
                        el.elManager.elContext,
                        expr,
                        type
                )
        val v = ve.getValue(el.elManager.elContext) ?: throw ELException()
        @Suppress("UNCHECKED_CAST")
        return v as T
    }

    @ColorInt
    @Throws(ELException::class)
    fun getColor(expr: String): Int {
        try {
            return parseColor(expr)
        } catch (e: IllegalArgumentException) {
            @Suppress("UNCHECKED_CAST")
            return scope(colorMap) {
                val value = getValue(expr, Any::class.java)
                if (value is Number) {
                    value.toInt()
                } else {
                    parseColor(value.toString())
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun createLayout(root: NodeInfo): Component {
        return createFromElement(root).single().build()
    }

    internal fun createFromElement(
            element: NodeInfo,
            upperVisibility: Int = View.VISIBLE
    ): List<Component.Builder<*>> {
        return transforms.getValue(element.type)
                .transform(
                        this,
                        element,
                        upperVisibility
                )
    }

    internal companion object {

        @Suppress("UNCHECKED_CAST")
        internal val colorMap = HashMap((Color::class.java
                .getDeclaredField("sColorNameMap")
                .apply { isAccessible = true }
                .get(null) as Map<String, Int>))

        internal val functions: List<Method> = Functions::class.java.declaredMethods
                .filter {
                    it.modifiers.let { mod ->
                        Modifier.isPublic(mod) && Modifier.isStatic(mod)
                    } && it.isAnnotationPresent(Prefix::class.java)
                }.map {
                    it.apply { it.isAccessible = true }
                }

        internal val transforms = mapOf(
                "Image" to ImageFactory,
                "Flex" to FlexFactory,
                "Text" to TextFactory,
                "Frame" to FrameFactory,
                "Native" to NativeFactory,
                "Scroller" to ScrollerFactory,
                "Empty" to EmptyFactory,
                "for" to ForTransform
        )
    }

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Prefix(val value: String)

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
        fun gradient(orientation: GradientDrawable.Orientation, vararg colors: String): GradientDrawable {
            return GradientDrawable(orientation, colors.map {
                parseColor(it)
            }.toIntArray())
        }
    }
}
