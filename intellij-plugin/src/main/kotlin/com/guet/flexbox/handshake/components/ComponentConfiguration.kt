package com.guet.flexbox.handshake.components

import com.google.gson.Gson

object ComponentConfiguration {

    private val components: Map<String, Map<String, AttributeInfo?>?>

    init {
        val gson = Gson()
        val classLoader = javaClass.classLoader
        val group = classLoader.getResourceAsStream(
            "flexml-components/package.json"
        )!!.reader()
        val arr = gson.fromJson(
            group, ComponentGroup::class.java
        ).components
        group.close()
        val componentInfoArray = arr.map {
            val input = classLoader.getResourceAsStream(it)!!.reader()
            val r = gson.fromJson(input, ComponentInfo::class.java)
            input.close()
            return@map r
        }

        fun loadAttribute(
            result: HashMap<String, AttributeInfo?>,
            com: ComponentInfo
        ) {
            com.attrs?.let {
                result.putAll(it)
            }
            val parent = com.parent?.let { name ->
                componentInfoArray.first {
                    name == it.name
                }
            }
            if (parent == null) {
                return
            } else {
                loadAttribute(result, parent)
            }
        }
        components = componentInfoArray.filter { !it.abstract }
            .map {
                val map = HashMap<String, AttributeInfo?>()
                loadAttribute(map, it)
                it.name to map
            }.toMap()
    }

    val allComponentNames: Set<String>
        get() = components.keys

    fun getAttributeInfoByComponentName(name: String): Map<String, AttributeInfo?> {
        return components[name] ?: emptyMap()
    }
}