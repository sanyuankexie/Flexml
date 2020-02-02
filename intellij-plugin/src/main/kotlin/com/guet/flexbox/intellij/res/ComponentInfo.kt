package com.guet.flexbox.intellij.res

data class ComponentInfo(
    val abstract: Boolean,
    val parent: String?,
    val name: String,
    val attrs: Map<String, AttributeInfo?>?
)