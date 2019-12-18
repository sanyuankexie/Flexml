package com.guet.flexbox.data

class LockedInfo(
        val type: String,
        val attrs: Map<String, Any>,
        val visibility: Boolean,
        val children: List<LockedInfo>
)