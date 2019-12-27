package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ToComponent
import com.guet.flexbox.el.PropsELContext


internal object Case : Declaration() {

    override val attributeSet: AttributeSet by create {
        bool("test")
    }

    override fun transform(
            c: ComponentContext,
            to: ToComponent<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        if (attrs.getValue("test") as Boolean) {
            return children.map {
                Toolkit.bindNode(c, it, data, upperVisibility)
            }.flatten()
        }
        return emptyList()
    }
}