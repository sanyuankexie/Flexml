package com.guet.flexbox.build

import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object If : Declaration() {

    override val attributeInfoSet: AttributeInfoSet by create {
        bool("test")
    }

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
        if (attrs.getValue("test") as Boolean) {
            return children.map {
                bindings.build(
                        it,
                        pageContext,
                        data,
                        upperVisibility,
                        other
                )
            }.flatten()
        }
        return emptyList()
    }
}