package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object Empty : Declaration(Common) {
    override val attributeInfoSet: AttributeInfoSet
        get() = emptyMap()

    override fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: HostingContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
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