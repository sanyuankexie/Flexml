package com.luke.skywalker.el

class PropsELResolver(
        private val props: Any,
        private val adapt: ELResolver
) : ELResolver() {

    override fun getValue(
            context: ELContext?,
            base: Any?,
            property: Any?
    ): Any? {
        return if (base == null) {
            adapt.getValue(context, props, property)
        } else {
            null
        }
    }

    override fun invoke(
            context: ELContext?,
            base: Any?,
            method: Any?,
            paramTypes: Array<out Class<*>>?,
            params: Array<out Any>?
    ): Any? {
        return if (base == null) {
            adapt.invoke(context, props, method, paramTypes, params)
        } else {
            null
        }
    }

    override fun getType(
            context: ELContext?,
            base: Any?,
            property: Any?
    ): Class<*>? {
        return if (base == null) {
            adapt.getType(context, props, property)
        } else {
            return null
        }
    }

    override fun setValue(context: ELContext?, base: Any?, property: Any?, value: Any?) {
        if (base == null) {
            adapt.setValue(context, props, property, value)
        }
    }

    override fun isReadOnly(context: ELContext?, base: Any?, property: Any?): Boolean = adapt.isReadOnly(context, base, property)

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*> = adapt.getCommonPropertyType(context, base)
}