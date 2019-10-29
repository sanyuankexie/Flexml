package com.guet.flexbox.build

import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.WidgetInfo
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
            el.defineFunction("fn", it.name, it)
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun enterScope(scope: Map<String, Any>) {
        el.elManager.elContext.enterLambdaScope(scope)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun exitScope() {
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
                parseColor(getValue(expr, String::class.java))
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun createLayout(root: WidgetInfo): Component {
        return createFromElement(root).single().build()
    }

    internal fun createFromElement(element: WidgetInfo): List<Component.Builder<*>> {
        val behavior = transforms.getValue(element.type)
        return behavior.transform(
                this,
                element,
                element.children.map {
                    createFromElement(it)
                }.flatten())
    }

    companion object {

        internal val colorMap: Map<String, Any>

        private val functions: List<Method> = Functions::class.java.declaredMethods
                .filter {
                    val mod = it.modifiers
                    Modifier.isPublic(mod) && Modifier.isStatic(mod)
                }.map {
                    it.apply {
                        it.isAccessible = true
                    }
                }


        private val transforms = HashMap<String, Transform>()

        init {
            @Suppress("UNCHECKED_CAST")
            colorMap = (Color::class.java
                    .getDeclaredField("sColorNameMap")
                    .apply { isAccessible = true }
                    .get(null) as Map<String, Int>)
                    .map {
                        it.key to it.value.toColorString()
                    }.toMap()
            transforms["Image"] = ImageFactory
            transforms["Flex"] = FlexFactory
            transforms["Text"] = TextFactory
            transforms["Frame"] = FrameFactory
            transforms["for"] = ForTransform
        }
    }

    private object Functions {
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

        @JvmName("gradient")
        @JvmStatic
        fun gradient(orientation: GradientDrawable.Orientation, vararg colors: String): GradientDrawable {
            return GradientDrawable(orientation, colors.map {
                parseColor(it)
            }.toIntArray())
        }
    }
}
