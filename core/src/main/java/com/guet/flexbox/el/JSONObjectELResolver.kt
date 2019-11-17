package com.guet.flexbox.el

import org.json.JSONObject

internal object JSONObjectELResolver : ELResolver() {

    override fun getValue(context: ELContext, base: Any?, property: Any?): Any? {
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            return base[property.toString()]
        }
        return null
    }

    override fun getType(context: ELContext, base: Any?, property: Any?): Class<*>? {
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            return base[property.toString()]?.javaClass
        }
        return null
    }

    override fun setValue(context: ELContext, base: Any?, property: Any?, value: Any?) {
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            base.put(property.toString(), value)
        }
    }

    override fun isReadOnly(context: ELContext, base: Any?, property: Any?): Boolean {
        return false
    }

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*>? {
        if (base is JSONObject) {
            return String::class.java
        }
        return null
    }
}