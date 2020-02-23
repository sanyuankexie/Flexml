package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import org.apache.commons.jexl3.JexlContext

object Empty : Declaration() {

    override val dataBinding: DataBinding
        get() = CommonProps.dataBinding

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
        return super.onBuildWidget(
                buildTool,
                attrs,
                children,
                factory,
                dataContext,
                pageContext,
                other,
                false
        )
    }
}