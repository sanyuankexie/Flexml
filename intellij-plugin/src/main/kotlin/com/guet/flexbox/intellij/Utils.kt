package com.guet.flexbox.intellij

import com.guet.flexbox.intellij.fileType.FlexmlFileType
import com.intellij.psi.PsiElement
import java.util.regex.Pattern

val PsiElement.isOnFlexmlFile: Boolean
    get() {
        return if (this.containingFile != null) {
            this.containingFile.name
                    .endsWith("." + FlexmlFileType.defaultExtension,
                            ignoreCase = true
                    )
        } else {
            false
        }
    }

private val pattern = Pattern.compile(
        "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?"
)

val String.isUrl: Boolean
    get() {
        return pattern.matcher(this).matches()
    }



