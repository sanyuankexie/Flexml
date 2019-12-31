package com.guet.flexbox.databinding

import android.view.View
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.PropsELContext

internal object Mount : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        this["type"] = object : AttributeInfo<Class<*>>() {
            override fun cast(pageContext: PageContext, props: PropsELContext, raw: String): Class<*>? {
                val text = props.scope(scope) {
                    props.tryGetValue<String>(raw)
                } ?: return null
                val type = Class.forName(text)
                check(View::class.java.isAssignableFrom(type)) { "$type is not as 'View' type" }
                return type
            }
        }
    }
}