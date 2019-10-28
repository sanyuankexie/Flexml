package com.guet.flexbox.build

import android.graphics.Color.*
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.WidgetInfo
import com.guet.flexbox.el.ELException
import com.guet.flexbox.el.ELManager
import com.guet.flexbox.el.ELProcessor
import lite.beans.Introspector
import java.io.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class BuildContext(val componentContext: ComponentContext, data: Any?) {

    private val el = ELProcessor()

    init {
        if (data != null) {
            enterScope(toMap(data))
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

        internal val colorMap = HashMap<String, Any>()

        private fun Long.toColorString(): String {
            return "#" + this.toInt().toString(16)
        }

        private fun Int.toColorString(): String {
            return "#" + this.toString(16)
        }

        private val functions: List<Method> = Functions::class.java.declaredMethods
                .filter {
                    val mod = it.modifiers
                    Modifier.isPublic(mod) && Modifier.isStatic(mod)
                }.map {
                    it.apply {
                        it.isAccessible = true
                    }
                }

        init {
            colorMap["black"] = BLACK.toColorString()
            colorMap["darkgray"] = DKGRAY.toColorString()
            colorMap["gray"] = GRAY.toColorString()
            colorMap["lightgray"] = LTGRAY.toColorString()
            colorMap["white"] = WHITE.toColorString()
            colorMap["red"] = RED.toColorString()
            colorMap["green"] = GREEN.toColorString()
            colorMap["blue"] = BLUE.toColorString()
            colorMap["yellow"] = YELLOW.toColorString()
            colorMap["cyan"] = CYAN.toColorString()
            colorMap["magenta"] = MAGENTA.toColorString()
            colorMap["aqua"] = 0xFF00FFFF.toColorString()
            colorMap["fuchsia"] = 0xFFFF00FF.toColorString()
            colorMap["darkgrey"] = DKGRAY.toColorString()
            colorMap["grey"] = GRAY.toColorString()
            colorMap["lightgrey"] = LTGRAY.toColorString()
            colorMap["lime"] = 0xFF00FF00.toColorString()
            colorMap["maroon"] = 0xFF800000.toColorString()
            colorMap["navy"] = 0xFF000080.toColorString()
            colorMap["olive"] = 0xFF808000.toColorString()
            colorMap["purple"] = 0xFF800080.toColorString()
            colorMap["silver"] = 0xFFC0C0C0.toColorString()
            colorMap["teal"] = 0xFF008080.toColorString()
        }

        private const val GOSN_CLASS_NAME = "com.google.gson.Gson"

        private val transforms = HashMap<String, Transform>()

        init {
            transforms["Image"] = ImageFactory
            transforms["Flex"] = FlexFactory
            transforms["Text"] = TextFactory
            transforms["Frame"] = FrameFactory
            transforms["for"] = ForTransform
        }

        private fun toMap(o: Any): Map<String, Any> {
            return if (o is Map<*, *> && o.keys.all { it is String }) {
                @Suppress("UNCHECKED_CAST")
                return o as Map<String, Any>
            } else if (o is InputStream
                    || o is ByteArray
                    || o is File
                    || o is Reader
                    || o is String) {
                val gsonType = Class.forName(GOSN_CLASS_NAME)
                val gson = gsonType.newInstance()
                var type: Class<*> = o.javaClass
                val input: Any
                when (o) {
                    is InputStream -> {
                        input = InputStreamReader(o)
                        type = Reader::class.java
                    }
                    is ByteArray -> {
                        input = InputStreamReader(ByteArrayInputStream(o))
                        type = Reader::class.java
                    }
                    is File -> {
                        input = InputStreamReader(FileInputStream(o))
                        type = Reader::class.java
                    }
                    else -> input = o
                }
                @Suppress("UNCHECKED_CAST")
                return gsonType.getMethod("fromJson", type, Class::class.java)
                        .invoke(gson, input, Map::class.java) as Map<String, Any>
            } else if (o.javaClass.declaredMethods.isEmpty()) {
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
