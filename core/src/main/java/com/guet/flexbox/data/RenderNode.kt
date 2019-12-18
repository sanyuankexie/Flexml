package com.guet.flexbox.data

class RenderNode(
        val type: String,
        val attrs: Map<String, Any>,
        val visibility: Boolean,
        val children: List<RenderNode>
)