package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object Empty : Declaration(Common) {
    override val attributeInfoSet: AttributeInfoSet
        get() = emptyMap()

    override fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: OutputFactory?,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        return super.onBuild(
                buildTool,
                attrs,
                children,
                factory,
                hostContext,
                data,
                false,
                other
        )
    }
}