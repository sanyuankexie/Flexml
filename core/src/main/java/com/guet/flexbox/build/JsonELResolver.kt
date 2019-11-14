package com.guet.flexbox.build

import com.guet.flexbox.el.ELContext
import com.guet.flexbox.el.ELResolver
import com.guet.flexbox.el.PropertyNotFoundException
import lite.beans.FeatureDescriptor
import org.json.JSONArray
import org.json.JSONObject

internal object JsonELResolver : ELResolver() {

    override fun getValue(context: ELContext, base: Any?, property: Any?): Any? {
        if (base is JSONArray) {
            context.setPropertyResolved(base, property)
            val idx = coerce(property)
            return if (idx < 0 || idx >= base.length()) {
                null
            } else base[idx]
        }
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            return base[property.toString()]
        }
        return null
    }

    override fun getType(context: ELContext, base: Any?, property: Any?): Class<*>? {
        if (base is JSONArray) {
            context.setPropertyResolved(base, property)
            val idx = coerce(property)
            return if (idx < 0 || idx >= base.length()) null else base[idx]?.javaClass
        }
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            return base[property.toString()]?.javaClass
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
        if (base is JSONObject) {
            context.setPropertyResolved(base, property)
            base.put(property.toString(), value)
        }
    }

    override fun isReadOnly(context: ELContext, base: Any?, property: Any?): Boolean {
        return false
    }

    override fun getFeatureDescriptors(context: ELContext?, base: Any?)
            : MutableIterator<FeatureDescriptor>? {
        if (base is JSONObject) {
            val feats = ArrayList<FeatureDescriptor>(base.length())
            base.keys().forEach { key ->
                val desc = FeatureDescriptor()
                desc.displayName = key.toString()
                desc.shortDescription = ""
                desc.isExpert = false
                desc.isHidden = false
                desc.name = key.toString()
                desc.isPreferred = true
                desc.setValue(RESOLVABLE_AT_DESIGN_TIME, true)
                desc.setValue(TYPE, key.javaClass)
                feats.add(desc)
            }
            return feats.iterator()
        }
        return null
    }

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*>? {
        return when (base) {
            is JSONArray -> Int::class.java
            is JSONObject -> String::class.java
            else -> null
        }
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