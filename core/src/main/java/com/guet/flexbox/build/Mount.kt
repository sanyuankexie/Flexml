package com.guet.flexbox.build

import android.view.View
import com.guet.flexbox.PageContext
import com.guet.flexbox.el.PropsELContext

internal object Mount : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        typed("type") { _: PageContext,
                        props: PropsELContext,
                        raw: String ->
            val text = props.tryGetValue<String>(raw) ?: return@typed null
            val type = Class.forName(text)
            check(View::class.java.isAssignableFrom(type)) {
                "$type is not as 'View' type"
            }
            return@typed type
        }
    }
}