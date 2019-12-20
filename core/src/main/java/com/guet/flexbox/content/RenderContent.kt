package com.guet.flexbox.content

import com.guet.flexbox.EventBridge

class RenderContent internal constructor(
        internal val bridge: EventBridge,
        internal val content: RenderNode
)