package com.guet.flexbox.el

import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.ObjectContext

class DataContext(
        engine: JexlEngine,
        wrapped: Any?
) : ObjectContext<Any>(
        engine,
        wrapped
) {

    override fun resolveNamespace(name: String?): Any {
        val obj = Functions[name]
        if (obj != null) {
            return obj
        }
        return super.resolveNamespace(name)
    }
}