package com.guet.flexbox.litho

import com.facebook.litho.Component
import com.guet.flexbox.EventBridge

class Page internal constructor(
        internal val component: Component,
        internal val eventBridge: EventBridge
)