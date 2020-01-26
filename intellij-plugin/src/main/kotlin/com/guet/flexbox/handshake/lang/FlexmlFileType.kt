package com.guet.flexbox.handshake.lang

import com.guet.flexbox.handshake.util.fileIcon
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.xml.XMLLanguage
import javax.swing.Icon

object FlexmlFileType : XmlLikeFileType(XMLLanguage.INSTANCE) {

    override fun getIcon(): Icon = fileIcon

    override fun getName(): String = "flexml dsl"

    override fun getDefaultExtension(): String = "flexml"

    override fun getDescription(): String = "flexml style dsl file"
}

