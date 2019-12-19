package com.guet.flexbox.build

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.data.RenderNode

object Inflater {

    fun inflate(c: ComponentContext, renderNode: RenderNode): Component? {
        return widgets[renderNode.type]?.create(
                c,
                renderNode,
                renderNode.children.mapNotNull {
                    inflate(c, it)
                }
        )
    }

    private val widgets = mapOf(
            "Flex" to Flex,
            "Image" to Image,
            "Native" to Native,
            "Scroller" to Scroller,
            "Stack" to Stack,
            "Text" to Text,
            "TextInput" to TextInput
    )
}