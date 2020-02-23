package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ScopeContext
import org.apache.commons.jexl3.JexlContext

object For : Declaration() {

    override val dataBinding by DataBinding
            .create {
                text("var")
                value("from")
                value("to")
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
        val name = attrs.getValue("var") as String
        val from = (attrs.getValue("from") as Float).toInt()
        val end = (attrs.getValue("to") as Float).toInt()
        return (from..end).map { index ->
            val scope = ScopeContext(mapOf(name to index), dataContext)
            buildTool.buildAll(
                    children,
                    scope,
                    pageContext,
                    other,
                    upperVisibility
            )
        }.flatten()
    }
}