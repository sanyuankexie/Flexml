package com.guet.flexbox.build

import android.graphics.Color.*
import androidx.annotation.ColorInt
import androidx.collection.ArrayMap
import com.facebook.litho.ComponentContext
import com.guet.flexbox.el.ELException
import com.guet.flexbox.el.ELManager
import com.guet.flexbox.el.ELProcessor
import lite.beans.Introspector
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
        return type.cast(v)!!
    }

    @ColorInt
    @Throws(ELException::class)
    fun getColor(expr: String): Int {
        try {
            return parseColor(expr)
        } catch (e: IllegalArgumentException) {
            @Suppress("UNCHECKED_CAST")
            try {
                enterScope(colorNameMap)
                return getValue(expr, Int::class.java)
            } finally {
                exitScope()
            }
        }
    }

    companion object {

        private val colorNameMap = ArrayMap<String, Any>()

        private val functions: List<Method>

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
            functions = Companion::class.java.declaredMethods
                    .filter {
                        val mod = it.modifiers
                        Modifier.isPublic(mod) && Modifier.isStatic(mod)
                    }
        }

        private const val GOSN_CLASS_NAME = "com.google.gson.Gson"


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

        @JvmStatic
        fun check(o: Any?): Boolean {
            return when (o) {
                is String -> o.isNotEmpty()
                is Collection<*> -> !o.isEmpty()
                is Number -> o.toInt() != 0
                else -> o != null
            }
        }
    }
}
