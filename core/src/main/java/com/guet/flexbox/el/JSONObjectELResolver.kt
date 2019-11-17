package com.guet.flexbox.el

import com.guet.flexbox.beans.FeatureDescriptor
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

    override fun getFeatureDescriptors(context: ELContext?, base: Any?)
            : MutableIterator<FeatureDescriptor>? {
        if (base is JSONObject) {
            val feats = ArrayList<FeatureDescriptor>(base.length())
            base.keys().forEach { key ->
                val desc = FeatureDescriptor()
                desc.name = key
                desc.setValue(RESOLVABLE_AT_DESIGN_TIME, true)
                desc.setValue(TYPE, key.javaClass)
                feats.add(desc)
            }
            return feats.iterator()
        }
        return null
    }

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*>? {
        if (base is JSONObject) {
            return String::class.java
        }
        return null
    }
}