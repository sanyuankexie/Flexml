package com.guet.flexbox.build

import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object Empty : Declaration(CommonProps) {
    override val attributeInfoSet: AttributeInfoSet
        get() = emptyMap()

    override fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory?,
            pageContext: PageContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        return super.onBuild(
                buildTool,
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