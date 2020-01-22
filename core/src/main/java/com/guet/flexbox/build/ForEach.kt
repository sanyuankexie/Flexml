package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.scope
import com.guet.flexbox.el.tryGetValue

object ForEach : Declaration() {

    override val attributeInfoSet: AttributeInfoSet by create {
        text("var")
        typed("items") { _, props, raw ->
            props.tryGetValue<List<Any>>(raw)
        }
    }

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
        val name = attrs.getValue("var") as String
        @Suppress("UNCHECKED_CAST")
        val items = attrs.getValue("items") as List<Any>
        return items.map {
            data.scope(mapOf(name to items)) {
                children.map {
                    buildTool.build(
                            it,
                            hostContext,
                            this,
                            upperVisibility,
                            other
                    )
                }
            }.flatten()
        }.flatten()
    }
}