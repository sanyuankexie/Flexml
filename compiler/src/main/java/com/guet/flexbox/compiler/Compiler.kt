package com.guet.flexbox.compiler

import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.File
import java.io.StringReader

open class Compiler<T>(
        private val factory: NodeFactory<T>
) {

    fun compile(layout: File): T {
        return transform(get().read(layout).rootElement)
    }

    fun compile(layout: String): T {
        return transform(get().read(StringReader(layout)).rootElement)
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

    private companion object SAX : ThreadLocal<SAXReader>() {
        override fun initialValue(): SAXReader {
            return SAXReader()
        }
    }
}