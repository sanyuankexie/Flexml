package com.guet.flexbox.databinding

import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.ComponentAdapt
import com.guet.flexbox.el.PropsELContext

internal object Empty : Declaration(Common) {
    override val attributeSet: AttributeSet
        get() = emptyMap()

    override fun transform(
            c: ComponentContext,
            adapt: ComponentAdapt<*>?,
            type: String,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<TemplateNode>,
            upperVisibility: Boolean
    ): List<Component> {
        return super.transform(c, adapt, type, attrs, data, children, false)
    }
}