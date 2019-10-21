package com.guet.flexbox.build

import android.graphics.Color.*
import androidx.annotation.ColorInt
import androidx.collection.ArrayMap
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

class BuildContext(
        val componentContext: ComponentContext,
        data: Any?) {

    private val el = ELProcessor()

    init {
        if (data != null) {
            enterScope(toMap(data))
        }
        functions.forEach {
            el.defineFunction("fn", it.name, it)
        }
    }

    fun enterScope(scope: Map<String, Any>) {
        el.elManager.elContext.enterLambdaScope(scope)
    }

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
                getValue(expr, Int::class.java)
            }
        }
    }

    companion object {

        private val colorNameMap = ArrayMap<String, Any>()

        private val functions: List<Method> = Func::class.java.declaredMethods
                .filter {
                    val mod = it.modifiers
                    Modifier.isPublic(mod) && Modifier.isStatic(mod)
                }

        init {
            colorNameMap["black"] = BLACK
            colorNameMap["darkgray"] = DKGRAY
            colorNameMap["gray"] = GRAY
            colorNameMap["lightgray"] = LTGRAY
            colorNameMap["white"] = WHITE
            colorNameMap["red"] = RED
            colorNameMap["green"] = GREEN
            colorNameMap["blue"] = BLUE
            colorNameMap["yellow"] = YELLOW
            colorNameMap["cyan"] = CYAN
            colorNameMap["magenta"] = MAGENTA
            colorNameMap["aqua"] = 0xFF00FFFF
            colorNameMap["fuchsia"] = 0xFFFF00FF
            colorNameMap["darkgrey"] = DKGRAY
            colorNameMap["grey"] = GRAY
            colorNameMap["lightgrey"] = LTGRAY
            colorNameMap["lime"] = 0xFF00FF00
            colorNameMap["maroon"] = 0xFF800000
            colorNameMap["navy"] = 0xFF000080
            colorNameMap["olive"] = 0xFF808000
            colorNameMap["purple"] = 0xFF800080
            colorNameMap["silver"] = 0xFFC0C0C0
            colorNameMap["teal"] = 0xFF008080
        }

        private const val GOSN_CLASS_NAME = "com.google.gson.Gson"


        private val behaviors = ArrayMap<String, Behavior>()

        init {
            behaviors["Image"] = ImageFactory
            behaviors["Flex"] = FlexFactory
            behaviors["Text"] = TextFactory
            behaviors["Frame"] = FrameFactory
            behaviors["for"] = ForBehavior
        }

        internal fun createFromElement(
                c: BuildContext,
                element: Element): List<Component.Builder<*>> {
            val behavior = behaviors.getValue(element.name)
            return behavior.apply(
                    c,
                    element,
                    element.attributes(),
                    element.elements().map {
                        createFromElement(c, it)
                    }.flatten())
        }

        @JvmStatic
        fun build(
                c: ComponentContext,
                document: Document,
                bind: Any?
        ): Component {
            return createFromElement(
                    BuildContext(c, bind),
                    document.rootElement
            ).single().build()
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
                if (o is InputStream) {
                    input = InputStreamReader(o)
                    type = Reader::class.java
                } else if (o is ByteArray) {
                    input = InputStreamReader(ByteArrayInputStream(o))
                    type = Reader::class.java
                } else if (o is File) {
                    input = InputStreamReader(FileInputStream(o))
                    type = Reader::class.java
                } else {
                    input = o;
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
}
