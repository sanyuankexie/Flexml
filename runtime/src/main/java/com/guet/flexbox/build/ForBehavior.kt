package com.guet.flexbox.build

import com.facebook.litho.Component
import org.dom4j.Attribute
import org.dom4j.Element
import java.util.*

internal object ForBehavior: Behavior {
    override fun apply(
            c: BuildContext,
            element: Element,
            attrs: List<Attribute>,
            children: List<Component.Builder<*>>): List<Component.Builder<*>> {
        val name = c.getValue(attrs["name"]!!, String::class.java)
        val from = c.getValue(attrs["from"]!!, Int::class.java)
        val to = c.getValue(attrs["to"]!!, Int::class.java)
        val elements = element.elements()
        return (from..to).map {
            c.enterScope(Collections.singletonMap(name, it))
            val list = elements.map { item ->
                Factory.createFromElement(c, item)
            }.flatten()
            c.exitScope()
            return@map list
        }.flatten()
    }
}
