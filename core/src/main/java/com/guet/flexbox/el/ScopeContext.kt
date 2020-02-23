package com.guet.flexbox.el

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext
import java.util.*

class ScopeContext(
        scope: Map<String, Any?>,
        private val inner: JexlContext
) : MapContext(Collections.unmodifiableMap(scope)), JexlContext.NamespaceResolver {

    override fun has(name: String?): Boolean {
        var h = super.has(name)
        if (!h) {
            h = inner.has(name)
        }
        return h
    }

    override fun get(name: String?): Any {
        val h = super.has(name)
        return if (h) {
            super.get(name)
        } else {
            inner.get(name)
        }
    }

    override fun set(name: String?, value: Any?) {
        val h = super.has(name)
        if (h) {
            super.set(name, value)
        } else {
            inner.set(name, value)
        }
    }

    override fun resolveNamespace(name: String?): Any? {
        return if (inner is JexlContext.NamespaceResolver) {
            inner.resolveNamespace(name)
        } else {
            null
        }
    }
}