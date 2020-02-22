package com.guet.flexbox.build

import com.guet.flexbox.transaction.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object If : Declaration() {

    override val attributeInfoSet: AttributeInfoSet by create {
        bool("test")
    }

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
        return if (attrs.getValue("test") as? Boolean != true) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    pageContext,
                    data,
                    upperVisibility,
                    other
            )
        }
    }
}