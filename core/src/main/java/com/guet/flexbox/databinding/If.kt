package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
import com.guet.flexbox.el.PropsELContext

internal object If : Declaration() {
    override val attributeSet: AttributeSet by create {
        bool("test")
    }

    override fun transform(
            c: Context,
            attrs: Map<String, Any>,
            data: PropsELContext,
            children: List<NodeInfo>,
            selfVisibility: Boolean
    ): List<LockedInfo> {
        if (attrs.getValue("test") as Boolean) {
            return children.mapNotNull {
                DataBindingUtils.bind(c, it, data, selfVisibility)
            }
        }
        return emptyList()
    }
}