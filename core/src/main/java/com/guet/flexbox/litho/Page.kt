package com.guet.flexbox.litho

import com.facebook.litho.Component
import com.guet.flexbox.EventBridge
import com.guet.flexbox.TemplateNode

class Page internal constructor(
        internal val template: TemplateNode,
        internal val component: Component,
        internal val eventBridge: EventBridge
)