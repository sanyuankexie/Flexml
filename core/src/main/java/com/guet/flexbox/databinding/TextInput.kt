package com.guet.flexbox.databinding

import com.guet.flexbox.PageContext
import com.guet.flexbox.el.LambdaExpression
import com.guet.flexbox.el.PropsELContext
import com.guet.flexbox.event.OnTextChangedEventHandler

internal object TextInput : Declaration(AbstractText) {
    override val attributeSet: AttributeSet by create {
        this["onTextChanged"] = object : AttributeInfo<OnTextChangedEventHandler>() {
            override fun cast(
                    pageContext: PageContext,
                    props: PropsELContext,
                    raw: String
            ): OnTextChangedEventHandler? {
                return props.tryGetValue<LambdaExpression>(raw)?.let {
                    OnTextChangedEventHandler(pageContext, props, it)
                }
            }
        }
    }
}