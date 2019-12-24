package com.guet.flexbox

import com.facebook.litho.Component

class PreloadPage internal constructor(
        val template: TemplateNode,
        val component: Component,
        val data: Any?,
        internal val exposed: ExposedPageContext
)