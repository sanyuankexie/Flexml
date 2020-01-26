package com.guet.flexbox.handshake.lang

import com.guet.flexbox.handshake.components.ComponentConfiguration
import com.guet.flexbox.handshake.util.isOnFlexmlFile
import com.guet.flexbox.handshake.util.tagIcon
import com.intellij.codeInsight.completion.XmlTagInsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.impl.source.xml.DefaultXmlTagNameProvider
import com.intellij.psi.xml.XmlTag

class FlexmlTagNameProvider : DefaultXmlTagNameProvider() {

    private val allTags = ComponentConfiguration.allComponentNames.map {
        LookupElementBuilder.create(it)
            .withInsertHandler(XmlTagInsertHandler.INSTANCE)
            .withBoldness(true)
            .withIcon(tagIcon)
            .withTypeText("flexml component")
    }

    override fun addTagNameVariants(
        elements: MutableList<LookupElement>,
        tag: XmlTag,
        prefix: String?
    ) {
        super.addTagNameVariants(elements, tag, prefix)
        if (!tag.isOnFlexmlFile) {
            return
        }
        if (tag.parentTag?.name == "when") {
            elements.addAll(
                listOf(
                    LookupElementBuilder.create("case")
                        .withInsertHandler(XmlTagInsertHandler.INSTANCE)
                        .withBoldness(true)
                        .withIcon(tagIcon)
                        .withTypeText("flexml component"),
                    LookupElementBuilder.create("else")
                        .withInsertHandler(XmlTagInsertHandler.INSTANCE)
                        .withBoldness(true)
                        .withIcon(tagIcon)
                        .withTypeText("flexml component")
                )
            )
        } else {
            elements.addAll(allTags)
        }
    }
}



