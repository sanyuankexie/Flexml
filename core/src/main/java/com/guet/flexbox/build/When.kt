package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext

object When : Declaration() {

    override val dataBinding: DataBinding
        get() = DataBinding.empty

    override fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperVisibility: Boolean
    ): List<Any> {
        var elseItem: TemplateNode? = null
        if (children.isNullOrEmpty()) {
            return emptyList()
        }
        for (item in children) {
            if (item.type == "case") {
                val itemAttrs = item.attrs
                if (itemAttrs != null && If.dataBinding.bind(
                                buildTool.engine,
                                dataContext,
                                eventDispatcher,
                                itemAttrs
                        )["test"] == true) {
                    return item.children?.let {
                        buildTool.buildAll(
                                children,
                                dataContext,
                                eventDispatcher,
                                other,
                                upperVisibility
                        )
                    } ?: emptyList()
                }
            } else if (item.type == "else" && elseItem == null) {
                elseItem = item
            }
        }
        return elseItem?.children?.let {
            buildTool.buildAll(
                    it,
                    dataContext,
                    eventDispatcher,
                    other,
                    upperVisibility
            )
        } ?: emptyList()
    }

}