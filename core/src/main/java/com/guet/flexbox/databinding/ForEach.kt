package com.guet.flexbox.databinding

import android.content.Context
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.guet.flexbox.ContentNode
import com.guet.flexbox.PageUtils
import com.guet.flexbox.build.ComponentAdapt
import com.guet.flexbox.el.PropsELContext

internal object ForEach : Declaration() {

    override val attributeSet: AttributeSet by create {
        text("var")
        this["items"] = object : AttributeInfo<List<Any>>() {
            override fun cast(c: Context, props: PropsELContext, raw: String): List<Any>? {
                return props.scope(scope) {
                    props.tryGetValue(raw, fallback)
                }
            }
        }
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
        val name = attrs.getValue("var") as String
        @Suppress("UNCHECKED_CAST")
        val items = attrs.getValue("items") as List<Any>
        return items.map {
            data.scope(mapOf(name to items)) {
                children.map {
                    PageUtils.bindNode(c, it, data, upperVisibility)
                }
            }.flatten()
        }.flatten()
    }
}