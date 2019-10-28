package com.guet.flexbox

data class WidgetInfo(
        val type: String,
        val attrs: Map<String, String>,
        val children: List<WidgetInfo>
)
