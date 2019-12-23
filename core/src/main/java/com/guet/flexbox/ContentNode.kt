package com.guet.flexbox

class ContentNode(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<ContentNode>?
)