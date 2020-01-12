package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

object Empty : Declaration(Common) {
    override val attributeInfoSet: AttributeInfoSet
        get() = emptyMap()

    override fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child<Any>> {
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