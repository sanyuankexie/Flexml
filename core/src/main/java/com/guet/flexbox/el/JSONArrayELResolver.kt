package com.guet.flexbox.el

import org.json.JSONArray

internal object JSONArrayELResolver : ELResolver() {

    override fun getValue(context: ELContext, base: Any?, property: Any?): Any? {
        if (base is JSONArray) {
            context.setPropertyResolved(base, property)
            val idx = coerce(property)
            return if (idx < 0 || idx >= base.length()) {
                null
            } else base[idx]
        }
        return null
    }

    override fun getType(context: ELContext, base: Any?, property: Any?): Class<*>? {
        if (base is JSONArray) {
            context.setPropertyResolved(base, property)
            val idx = coerce(property)
            return if (idx < 0 || idx >= base.length()) null else base[idx]?.javaClass
        }
        return null
    }

    override fun setValue(context: ELContext, base: Any?, property: Any?, value: Any?) {
        if (base is JSONArray) {
            context.setPropertyResolved(base, property)
            val idx = coerce(property)
            checkBounds(base, idx)
            base.put(idx, value)
        }
    }

    override fun isReadOnly(context: ELContext, base: Any?, property: Any?): Boolean {
        return false
    }

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*>? {
        if (base is JSONArray) {
            return Int::class.javaObjectType
        }
        return null
    }

    private fun coerce(property: Any?): Int {
        if (property is Number) {
            return property.toInt()
        }
        if (property is Char) {
            return property.toInt()
        }
        if (property is Boolean) {
            return if (property) 1 else 0
        }
        if (property is String) {
            return property.toInt()
        }
        throw IllegalArgumentException(property?.toString() ?: "null")
    }

    private fun checkBounds(base: JSONArray, idx: Int) {
        if (idx < 0 || idx >= base.length()) {
            throw PropertyNotFoundException(ArrayIndexOutOfBoundsException(idx).message)
        }
    }
}