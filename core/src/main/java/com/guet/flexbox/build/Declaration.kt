package com.guet.flexbox.build

import android.util.ArrayMap
import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.build.attrsinfo.AttributeInfo
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
            hostContext: HostContext,
            data: ELContext
    ): AttributeSet {
        return if (rawAttrs.isNullOrEmpty()) {
            emptyMap()
        } else {
            ArrayMap<String, Any>(rawAttrs.size).also {
                for ((key, raw) in rawAttrs) {
                    val result = this[key]?.cast(hostContext, data, raw)
                    if (result != null) {
                        it[key] = result
                    }
                }
            }
        }
    }

    open fun onBuild(
            buildTool: BuildTool,
            attrs: AttributeSet,
            children: List<TemplateNode>,
            factory: ViewFactory?,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        return parent?.onBuild(
                buildTool,
                attrs,
                children,
                factory,
                hostContext,
                data,
                upperVisibility,
                other
        ) ?: throw UnsupportedOperationException()
    }

    fun transform(
            bindings: BuildTool,
            rawAttrs: Map<String, String>,
            children: List<TemplateNode>,
            factory: ViewFactory?,
            hostContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            other: Any
    ): List<Child> {
        val attrs = onBind(
                rawAttrs,
                hostContext,
                data
        )
        return onBuild(
                bindings,
                attrs,
                children,
                factory,
                hostContext,
                data,
                upperVisibility,
                other
        )
    }
}