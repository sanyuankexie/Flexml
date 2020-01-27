package com.guet.flexbox.compiler

import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.File

open class Compiler<T>(
        private val factory: NodeFactory<T>
) {

    fun compile(layout: String): T {
        return transform(
                sax.read(
                        File(layout)
                ).rootElement
        )
    }

    private fun transform(element: Element): T {
        return factory.createNode(
                element.name,
                element.attributes().map {
                    it.name to it.value
                }.toMap(), element.elements().map {
            transform(it)
        })
    }

    companion object{
        private val sax = SAXReader()
    }
}