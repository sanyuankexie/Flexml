package com.guet.flexbox.handshake.bundle

data class ComponentInfo(
    val abstract: Boolean,
    val parent: String?,
    val name: String,
    val attrs: Map<String, AttributeInfo?>?
)