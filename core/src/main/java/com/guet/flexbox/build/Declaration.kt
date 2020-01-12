package com.guet.flexbox.build

import android.util.ArrayMap
import com.guet.flexbox.PageContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.PropsELContext

abstract class Declaration(
        private val parent: Declaration? = null
) {

    internal abstract val attributeInfoSet: AttributeInfoSet

    private operator fun get(name: String): AttributeInfo<*>? {
        val v = attributeInfoSet[name]
        if (v != null) {
            return v
        }
        if (parent != null) {
            return parent[name]
        }
        return null
    }

    open fun onBind(
            rawAttrs: Map<String, String>?,
            pageContext: PageContext,
            data: PropsELContext
    ): AttributeSet {
        val expose = if (rawAttrs.isNullOrEmpty()) {
            emptyMap<String, Any>()
        } else {
            ArrayMap<String, Any>(rawAttrs.size).also {
                for ((key, raw) in rawAttrs) {
                    val result = this[key]?.cast(pageContext, data, raw)
                    if (result != null) {
                        it[key] = result
                    }
                }
            }
        }
        return expose
    }

    open fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child<Any>> {
        return parent?.onBuild(
                bindings,
                attrs,
                children,
                factory,
                pageContext,
                data,
                upperVisibility,
                other
        ) ?: throw UnsupportedOperationException()
    }

    fun transform(
            bindings: BuildUtils,
            rawAttrs: Map<String, String>,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: PageContext,
            data: PropsELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child<Any>> {
        val attrs = onBind(
                rawAttrs,
                pageContext,
                data
        )
        return onBuild(
                bindings,
                attrs,
                children,
                factory,
                pageContext,
                data,
                upperVisibility,
                other
        )
    }
}