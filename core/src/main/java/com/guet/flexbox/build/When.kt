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
            factory: OutputFactory?,
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
                    return item.children?.map {
                        buildTool.build(
                                it,
                                hostContext,
                                data,
                                upperVisibility,
                                other
                        )
                    }?.flatten() ?: emptyList()
                }
            } else if (item.type == "else" && elseItem == null) {
                elseItem = item
            }
        }
        return elseItem?.children?.map {
            buildTool.build(
                    it,
                    hostContext,
                    data,
                    upperVisibility,
                    other
            )
        }?.flatten() ?: emptyList()
    }

}