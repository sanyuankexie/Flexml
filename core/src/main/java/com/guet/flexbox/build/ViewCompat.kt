package com.guet.flexbox.build

import android.content.Context
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

object ViewCompat : Declaration(Common) {

    override val attributeSet: AttributeSet
        get() = emptyMap()

    override fun onBind(
            rawAttrs: Map<String, String>?,
            pageContext: PageContext,
            data: PropsELContext
    ): Map<String, Any> {
        var attrs = super.onBind(rawAttrs, pageContext, data)
        if (!rawAttrs.isNullOrEmpty()) {
            val otherRawAttrs = HashMap(rawAttrs)
            otherRawAttrs.keys.removeAll(attrs.keys)
            if (otherRawAttrs.isNotEmpty()) {
                attrs = ViewCompatExtra(attrs, otherRawAttrs to data)
            }
        }
        return attrs
    }

    override fun onBuild(
            bindings: BuildUtils,
            attrs: Map<String, Any>,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Any> {
        return super.onBuild(
                bindings,
                attrs,
                emptyList(),
                factory,
                pageContext,
                data,
                upperVisibility,
                other
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun obtainAttributes(
            c: Context,
            map: Map<String, Any>
    ): android.util.AttributeSet {
        val (other, data) = (map as ViewCompatExtra).extra
        return AndroidAttributeSet(
                c,
                other,
                data
        )
    }
}