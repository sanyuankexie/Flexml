package com.guet.flexbox.compiler

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.InputStream
import java.io.StringReader

object Compiler {

    private val sax = SAXReader()

    fun compile(layout: String): String {
        return toJson(sax.read(StringReader(layout)).rootElement).toString()
    }

    fun compile(layout: InputStream): String {
        return toJson(sax.read(layout).rootElement).toString()
    }

    private fun toJson(element: Element): JsonObject {
        val obj = JsonObject()
        obj.addProperty("type", element.name)
        element.attributes().apply {
            if (isNotEmpty()) {
                val attrs = JsonObject()
                forEach { attrs.addProperty(it.name, it.value) }
                obj.add("attrs", attrs)
            }
        }
        element.elements().apply {
            if (isNotEmpty()) {
                val children = JsonArray()
                map { toJson(it) }.forEach { children.add(it) }
                obj.add("children", children)
            }
        }
        return obj
    }
}