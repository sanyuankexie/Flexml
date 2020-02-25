package com.guet.flexbox.el

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext

class ScopeContext(
        scope: Map<String, Any?>,
        private val inner: JexlContext
) : MapContext(scope), JexlContext.NamespaceResolver {

    override fun has(name: String?): Boolean {
        var h = super.has(name)
        if (!h) {
            h = inner.has(name)
        }
        return h
    }

    override fun get(name: String?): Any? {
        val h = super.has(name)
        return if (h) {
            super.get(name)
        } else {
            inner.get(name)
        }
    }

    override fun set(name: String?, value: Any?) {
        val h = super.has(name)
        if (!h) {
            inner.set(name, value)
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun resolveNamespace(name: String?): Any? {
        return if (
                !name.isNullOrEmpty()
                && inner is JexlContext.NamespaceResolver
        ) {
            inner.resolveNamespace(name)
        } else {
            null
        }
    }
}