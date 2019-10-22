package com.guet.flexbox.build

import android.graphics.Color.*
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.el.ELException
import com.guet.flexbox.el.ELManager
import com.guet.flexbox.el.ELProcessor
import lite.beans.Introspector
import org.dom4j.Document
import org.dom4j.Element
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
            return scope(colorNameMap) {
                parseColor(getValue(expr, String::class.java))
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun createLayout(document: Document): Component {
        return createFromElement(document.rootElement).single().build()
    }

    internal fun createFromElement(element: Element): List<Component.Builder<*>> {
        val behavior = behaviors.getValue(element.name)
        return behavior.transform(
                this,
                element,
                element.attributes(),
                element.elements().map {
                    createFromElement(it)
                }.flatten())
    }

    companion object {

        private val colorNameMap = HashMap<String, Any>()

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
                }

        init {
            colorNameMap["black"] = BLACK.toColorString()
            colorNameMap["darkgray"] = DKGRAY.toColorString()
            colorNameMap["gray"] = GRAY.toColorString()
            colorNameMap["lightgray"] = LTGRAY.toColorString()
            colorNameMap["white"] = WHITE.toColorString()
            colorNameMap["red"] = RED.toColorString()
            colorNameMap["green"] = GREEN.toColorString()
            colorNameMap["blue"] = BLUE.toColorString()
            colorNameMap["yellow"] = YELLOW.toColorString()
            colorNameMap["cyan"] = CYAN.toColorString()
            colorNameMap["magenta"] = MAGENTA.toColorString()
            colorNameMap["aqua"] = 0xFF00FFFF.toColorString()
            colorNameMap["fuchsia"] = 0xFFFF00FF.toColorString()
            colorNameMap["darkgrey"] = DKGRAY.toColorString()
            colorNameMap["grey"] = GRAY.toColorString()
            colorNameMap["lightgrey"] = LTGRAY.toColorString()
            colorNameMap["lime"] = 0xFF00FF00.toColorString()
            colorNameMap["maroon"] = 0xFF800000.toColorString()
            colorNameMap["navy"] = 0xFF000080.toColorString()
            colorNameMap["olive"] = 0xFF808000.toColorString()
            colorNameMap["purple"] = 0xFF800080.toColorString()
            colorNameMap["silver"] = 0xFFC0C0C0.toColorString()
            colorNameMap["teal"] = 0xFF008080.toColorString()
        }

        private const val GOSN_CLASS_NAME = "com.google.gson.Gson"

        private val behaviors = HashMap<String, Transform>()

        init {
            behaviors["Image"] = ImageFactory
            behaviors["Flex"] = FlexFactory
            behaviors["Text"] = TextFactory
            behaviors["Frame"] = FrameFactory
            behaviors["for"] = ForTransform
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

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    object Functions {
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
