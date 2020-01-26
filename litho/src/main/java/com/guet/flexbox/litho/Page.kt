package com.guet.flexbox.litho

import com.facebook.litho.Component
import com.guet.flexbox.ForwardContext
import com.guet.flexbox.TemplateNode

class Page internal constructor(
        internal val template: TemplateNode,
        internal var display: Component,
        internal val forward: ForwardContext
)