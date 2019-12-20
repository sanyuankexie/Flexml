package com.guet.flexbox.content

class DynamicNode(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<DynamicNode>?
)