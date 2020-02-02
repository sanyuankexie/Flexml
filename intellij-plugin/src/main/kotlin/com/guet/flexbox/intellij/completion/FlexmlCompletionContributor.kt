package com.guet.flexbox.intellij.completion

import com.guet.flexbox.intellij.res.ComponentInfoBundle
import com.guet.flexbox.intellij.res.Icons
import com.guet.flexbox.intellij.res.SupportType
import com.guet.flexbox.intellij.isOnFlexmlFile
import com.guet.flexbox.intellij.isUrl
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.impl.source.xml.XmlAttributeReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElementType
import com.intellij.util.ProcessingContext
import com.intellij.xml.util.ColorIconCache
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor


class FlexmlCompletionContributor : CompletionContributor() {

    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                object : CompletionProvider<CompletionParameters>() {
                    override fun addCompletions(
                            parameters: CompletionParameters,
                            context: ProcessingContext,
                            result: CompletionResultSet
                    ) {
                        if (!parameters.position.isOnFlexmlFile) {
                            return
                        }
                        val reference = parameters
                                .position
                                .containingFile
                                .findReferenceAt(parameters.offset)
                        if (reference is XmlAttributeReference) {
                            val declarationTag = reference.element.parent
                            if (declarationTag?.name == "case") {
                                result.addElement(
                                        LookupElementBuilder.create("test")
                                                .withInsertHandler(XmlAttributeInsertHandler.INSTANCE)
                                                .withBoldness(true)
                                                .withIcon(Icons.tagIcon)
                                                .withTypeText("case attribute (required)")
                                )
                                return
                            }
                            result.addAllElements(
                                    ComponentInfoBundle
                                            .getAttributeInfoByComponentName(declarationTag.name)
                                            .toMutableMap()
                                            .apply {
                                                declarationTag.attributes.forEach {
                                                    remove(it.name)
                                                }
                                            }.map {
                                                val (name, value) = it
                                                LookupElementBuilder.create(name)
                                                        .withInsertHandler(XmlAttributeInsertHandler.INSTANCE)
                                                        .withBoldness(true)
                                                        .withIcon(Icons.tagIcon)
                                                        .withTypeText(
                                                                "${declarationTag.name} attribute" +
                                                                        if (value?.required == true) {
                                                                            "(required)"
                                                                        } else {
                                                                            ""
                                                                        }
                                                        )
                                            })
                        }
                    }
                })
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                object : CompletionProvider<CompletionParameters>() {
                    override fun addCompletions(
                            parameters: CompletionParameters,
                            context: ProcessingContext,
                            result: CompletionResultSet
                    ) {
                        if (!parameters.position.isOnFlexmlFile) {
                            return
                        }
                        val position = parameters.position
                        if (position.node.elementType === XmlElementType.XML_ATTRIBUTE_VALUE_TOKEN) {
                            val attr = PsiTreeUtil.getParentOfType(
                                    position,
                                    XmlAttribute::class.java
                            )
                            if (attr != null) {
                                val attrValues = ComponentInfoBundle
                                        .getAttributeInfoByComponentName(attr.parent.name)[attr.name]
                                val support = attrValues?.support ?: return
                                val values = attrValues.values
                                if (support.contains(SupportType.VALUES) && !values.isNullOrEmpty()) {
                                    result.addAllElements(values.map {
                                        LookupElementBuilder.create(it)
                                                .withIcon(AllIcons.FileTypes.Xml)
                                    })
                                }
                                if (support.contains(SupportType.URL)) {
                                    val trans = Toolkit.getDefaultToolkit()
                                            .systemClipboard
                                            .getContents(null)
                                    if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                        try {
                                            val text = trans.getTransferData(DataFlavor.stringFlavor) as? String
                                            if (!text.isNullOrEmpty() && text.isUrl) {
                                                result.addElement(
                                                        LookupElementBuilder.create(text)
                                                                .withIcon(AllIcons.FileTypes.Html)
                                                )
                                            }
                                        } catch (e: Exception) {

                                        }
                                    }
                                }
                                if (support.contains(SupportType.BOOL)) {
                                    result.addElement(
                                            LookupElementBuilder.create("true")
                                                    .withIcon(AllIcons.FileTypes.Xml)
                                    )
                                    result.addElement(
                                            LookupElementBuilder.create("false")
                                                    .withIcon(AllIcons.FileTypes.Xml)
                                    )
                                }
                                if (support.contains(SupportType.COLORS)) {
                                    result.addAllElements(Icons.colors.map {
                                        val icon = ColorIconCache.getIconCache()
                                                .getIcon(Color(it.value), 16)
                                        LookupElementBuilder.create(it.key)
                                                .withTypeText("#" + Integer.toHexString(it.value))
                                                .withIcon(icon)
                                    })
                                }
                            }
                        }
                    }
                })
    }
}