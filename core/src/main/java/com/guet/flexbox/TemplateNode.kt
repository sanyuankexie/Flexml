package com.guet.flexbox

class TemplateNode(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<TemplateNode>?
)