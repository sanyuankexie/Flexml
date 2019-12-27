package com.guet.flexbox

import com.facebook.litho.Component

class Page internal constructor(
        val template: TemplateNode,
        val data: Any?,
        internal val component: Component,
        internal val exposed: ExposedPageContext
)