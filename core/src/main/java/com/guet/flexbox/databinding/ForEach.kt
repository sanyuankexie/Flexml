package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
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
            c: Context,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<NodeInfo>,
            selfVisibility: Boolean
    ): List<LockedInfo> {
        val name = attrs.getValue("var") as String
        @Suppress("UNCHECKED_CAST")
        val items = attrs.getValue("items") as List<Any>
        return items.map {
            data.scope(mapOf(name to items)) {
                children.mapNotNull {
                    DataBindingUtils.bind(c, it, data, selfVisibility)
                }
            }
        }.flatten()
    }
}