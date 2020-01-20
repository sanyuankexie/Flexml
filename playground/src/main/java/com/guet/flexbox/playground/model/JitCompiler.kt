package com.guet.flexbox.playground.model

import com.guet.flexbox.TemplateNode
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.StringReader

object JitCompiler {

    private val sax = SAXReader()

    fun compile(layout: String): TemplateNode {
        return toTemplateNode(sax.read(StringReader(layout)).rootElement)
    }

    private fun toTemplateNode(element: Element): TemplateNode {
        val attrs = element.attributes().map {
            it.name to it.value
        }.toMap()
        val children = element.elements().map {
            toTemplateNode(it)
        }
        return TemplateNode(element.name, attrs, children)
    }
}