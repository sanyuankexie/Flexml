package com.guet.flexbox

data class NodeInfo(
        val type: String,
        val attrs: Map<String, String>?,
        val children: List<NodeInfo>?
)
