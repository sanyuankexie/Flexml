package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

object Empty : Declaration(Common) {
    override val attributeSet: AttributeSet
        get() = emptyMap()

    override fun onBuild(
            bindings: BuildUtils,
            attrs: Map<String, Any>,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        return super.onBuild(
                bindings,
                attrs,
                children,
                factory,
                pageContext,
                data,
                false,
                other
        )
    }
}