package com.guet.flexbox.build

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.eventsystem.EventTarget
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlEngine

object Empty : Declaration() {

    override val dataBinding: DataBinding
        get() = CommonProps.dataBinding

    override fun onBuildWidget(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: RenderNodeFactory<*>?,
            engine: JexlEngine,
            dataContext: JexlContext,
            eventDispatcher: EventTarget,
            other: Any?,
            upperVisibility: Boolean
    ): List<Any> {
        return super.onBuildWidget(
                buildTool,
                attrs,
                children,
                factory,
                engine,
                dataContext,
                eventDispatcher,
                other,
                false
        )
    }
}