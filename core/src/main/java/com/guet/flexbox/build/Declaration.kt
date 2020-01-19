package com.guet.flexbox.build

import android.util.ArrayMap
import com.guet.flexbox.HostingContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

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
            pageContext: HostingContext,
            data: ELContext
    ): AttributeSet {
        return if (rawAttrs.isNullOrEmpty()) {
            emptyMap()
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
    }

    open fun onBuild(
            bindings: BuildUtils,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: Factory?,
            pageContext: HostingContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
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
            pageContext: HostingContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
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