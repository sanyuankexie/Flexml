package com.guet.flexbox.el

internal class PropsELResolver(
        private val props: Any,
        private val adapt: ELResolver
) : ELResolver() {

    override fun getValue(
            context: ELContext,
            base: Any?,
            property: Any?
    ): Any? {
        if (base != null || property !is String) {
            return null
        }
        return adapt.getValue(context, props, property)
    }

    override fun getType(
            context: ELContext?,
            base: Any?,
            property: Any?
    ): Class<*>? {
        if (base != null || property !is String) {
            return null
        }
        return adapt.getType(context, props, property)
    }

    override fun setValue(
            context: ELContext?,
            base: Any?,
            property: Any?,
            value: Any?
    ) {
        if (base != null || property !is String) {
            return
        }
        adapt.setValue(context, props, property, value)
    }

    override fun isReadOnly(context: ELContext?, base: Any?, property: Any?): Boolean = true

    override fun getCommonPropertyType(context: ELContext?, base: Any?): Class<*> = adapt.getCommonPropertyType(context, base)
}