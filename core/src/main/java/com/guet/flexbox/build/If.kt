package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import org.apache.commons.jexl3.JexlContext

object If : Declaration() {

    override val dataBinding by DataBinding
            .create {
                bool("test")
            }

    override fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            dataContext: JexlContext,
            pageContext: PageContext,
            other: Any?,
            upperVisibility: Boolean
    ): List<Any> {
        return if (attrs.getValue("test") as? Boolean != true) {
            emptyList()
        } else {
            buildTool.buildAll(
                    children,
                    dataContext,
                    pageContext,
                    other,
                    upperVisibility
            )
        }
    }
}