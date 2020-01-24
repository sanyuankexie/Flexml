package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

object When : Declaration() {

    override val attributeInfoSet: AttributeInfoSet
        get() = emptyMap()

    override fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: ViewFactory?,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        var elseItem: TemplateNode? = null
        if (children.isNullOrEmpty()) {
            return emptyList()
        }
        for (item in children) {
            if (item.type == "case") {
                val itemAttrs = item.attrs
                if (itemAttrs != null && If.onBind(
                                itemAttrs,
                                hostContext,
                                data
                        )["test"] == true) {
                    return item.children?.let {
                        buildTool.buildAll(
                                children,
                                hostContext,
                                data,
                                upperVisibility,
                                other
                        )
                    } ?: emptyList()
                }
            } else if (item.type == "else" && elseItem == null) {
                elseItem = item
            }
        }
        return elseItem?.children?.let {
            buildTool.buildAll(it,
                    hostContext,
                    data,
                    upperVisibility,
                    other
            )
        } ?: emptyList()
    }

}