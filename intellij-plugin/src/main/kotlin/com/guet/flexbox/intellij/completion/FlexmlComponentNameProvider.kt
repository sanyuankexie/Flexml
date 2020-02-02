package com.guet.flexbox.intellij.completion

import com.guet.flexbox.intellij.res.ComponentInfoBundle
import com.guet.flexbox.intellij.res.Icons
import com.guet.flexbox.intellij.isOnFlexmlFile
import com.intellij.codeInsight.completion.XmlTagInsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.impl.source.xml.DefaultXmlTagNameProvider
import com.intellij.psi.xml.XmlTag

class FlexmlComponentNameProvider : DefaultXmlTagNameProvider() {

    private val allTags = ComponentInfoBundle.allComponentNames.map {
        LookupElementBuilder.create(it)
            .withInsertHandler(XmlTagInsertHandler.INSTANCE)
            .withBoldness(true)
            .withIcon(Icons.tagIcon)
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
                        .withIcon(Icons.tagIcon)
                        .withTypeText("flexml component"),
                    LookupElementBuilder.create("else")
                        .withInsertHandler(XmlTagInsertHandler.INSTANCE)
                        .withBoldness(true)
                        .withIcon(Icons.tagIcon)
                        .withTypeText("flexml component")
                )
            )
        } else {
            elements.addAll(allTags)
        }
    }
}



