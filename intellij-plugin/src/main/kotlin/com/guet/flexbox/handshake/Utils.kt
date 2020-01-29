package com.guet.flexbox.handshake

import com.guet.flexbox.handshake.filetype.FlexmlFileType
import com.intellij.psi.PsiElement
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

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


private val formatter = SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss",
        Locale.CHINA
)

val nowTime: String
    get() {
        return formatter.format(Calendar.getInstance().time)
    }


