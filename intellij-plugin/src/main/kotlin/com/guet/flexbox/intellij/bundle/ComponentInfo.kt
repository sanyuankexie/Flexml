package com.guet.flexbox.intellij.bundle

data class ComponentInfo(
    val abstract: Boolean,
    val parent: String?,
    val name: String,
    val attrs: Map<String, AttributeInfo?>?
)