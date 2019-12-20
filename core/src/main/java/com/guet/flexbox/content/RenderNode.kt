package com.guet.flexbox.content

internal class RenderNode(
        val type: String,
        val attrs: Map<String, Any>,
        val visibility: Boolean,
        val children: List<RenderNode>
)