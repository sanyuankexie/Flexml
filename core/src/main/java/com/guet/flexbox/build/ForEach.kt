package com.guet.flexbox.build

import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.el.scope

object ForEach : Declaration() {

    override val attributeInfoSet: AttributeInfoSet by create {
        text("var")
        typed("items") { _, props, raw ->
            props.tryGetValue<List<Any>>(raw)
        }
    }

    override fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        val name = attrs.getValue("var") as String
        @Suppress("UNCHECKED_CAST")
        val items = attrs.getValue("items") as List<Any>
        return items.map {
            data.scope(mapOf(name to items)) {
                children.map {
                    bindings.bindNode(
                            it,
                            pageContext,
                            data,
                            upperVisibility,
                            other
                    )
                }
            }.flatten()
        }.flatten()
    }
}