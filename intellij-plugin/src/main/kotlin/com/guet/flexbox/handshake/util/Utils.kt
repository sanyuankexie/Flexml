package com.guet.flexbox.handshake.util

import com.guet.flexbox.handshake.lang.FlexmlFileType
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

val PsiElement.isOnFlexmlFile: Boolean
    get() {
        return if (this.containingFile != null) {
            this.containingFile.name.toLowerCase()
                .endsWith("." + FlexmlFileType.defaultExtension)
        } else {
            false
        }
    }

val fileIcon = IconLoader.getIcon("/icons/icon_file.png")

val tagIcon = IconLoader.getIcon("/icons/icon_tag.png")

val String.isUrl: Boolean
    get() {
        val regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
        return match(regex, this)
    }

private fun match(regex: String, str: String): Boolean {
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(str)
    return matcher.matches()
}

private val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

val nowTime: String
    get() {
        return formatter.format(Calendar.getInstance().time)
    }

val colors: Map<String, Int> = HashMap<String, Int>().apply {
    this.put("black", 0xFF000000.toInt())
    this.put("darkgray", 0xFF444444.toInt())
    this.put("gray", 0xFF888888.toInt())
    this.put("lightgray", 0xFFCCCCCC.toInt())
    this.put("white", 0xFFFFFFFF.toInt())
    this.put("red", 0xFFFF0000.toInt())
    this.put("green", 0xFF00FF00.toInt())
    this.put("blue", 0xFF0000FF.toInt())
    this.put("yellow", 0xFFFFFF00.toInt())
    this.put("cyan", 0xFF00FFFF.toInt())
    this.put("magenta", 0xFFFF00FF.toInt())
    this.put("aqua", 0xFF00FFFF.toInt())
    this.put("fuchsia", 0xFFFF00FF.toInt())
    this.put("lime", 0xFF00FF00.toInt())
    this.put("maroon", 0xFF800000.toInt())
    this.put("navy", 0xFF000080.toInt())
    this.put("olive", 0xFF808000.toInt())
    this.put("purple", 0xFF800080.toInt())
    this.put("silver", 0xFFC0C0C0.toInt())
    this.put("teal", 0xFF008080.toInt())
}
