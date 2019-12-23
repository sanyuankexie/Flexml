package com.guet.flexbox

import com.facebook.litho.Component

class PreloadPage internal constructor(
        val content: ContentNode,
        val component: Component,
        val data: Any?,
        internal val dispatcher: PageEventBridgeAdapter
)