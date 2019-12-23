package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.ContentNode
import com.guet.flexbox.PageUtils
import com.guet.flexbox.build.ComponentAdapt
import com.guet.flexbox.el.PropsELContext

internal object If : Declaration() {
    override val attributeSet: AttributeSet by create {
        bool("test")
    }

    override fun transform(
            c: ComponentContext,
            adapt: ComponentAdapt<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<ContentNode>,
            upperVisibility: Boolean
    ): List<Component> {
        if (attrs.getValue("test") as Boolean) {
            return children.map {
                PageUtils.bindNode(c, it, data, upperVisibility)
            }.flatten()
        }
        return emptyList()
    }
}