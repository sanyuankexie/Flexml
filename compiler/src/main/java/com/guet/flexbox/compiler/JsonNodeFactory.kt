package com.guet.flexbox.compiler

import com.google.gson.JsonArray
import com.google.gson.JsonObject

object JsonNodeFactory : NodeFactory<JsonObject> {
    override fun createNode(
            type: String,
            attrs: Map<String, String>,
            children: List<JsonObject>
    ): JsonObject {
        val obj = JsonObject()
        obj.addProperty("type", type)
        if (attrs.isNotEmpty()) {
            val objAttrs = JsonObject()
            obj.add("attrs", objAttrs)
            attrs.forEach {
                objAttrs.addProperty(it.key, it.value)
            }
        }
        if (children.isNotEmpty()) {
            val objChildren = JsonArray()
            obj.add("children", objChildren)
            children.forEach {
                objChildren.add(it)
            }
        }
        return obj
    }
}