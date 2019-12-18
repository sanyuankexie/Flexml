package com.guet.flexbox.data

class LayoutNode(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<LayoutNode>?
)