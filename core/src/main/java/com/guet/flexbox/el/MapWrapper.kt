package com.guet.flexbox.el

internal class MapWrapper(private val map: MutableMap<String, Any?>) : BeanNameResolver() {

    override fun setBeanValue(beanName: String, value: Any?) {
        map[beanName] = value
    }

    override fun getBean(beanName: String?): Any? {
        return map[beanName]
    }

    override fun isNameResolved(beanName: String?): Boolean {
        return map.containsKey(beanName)
    }

    override fun isReadOnly(beanName: String?): Boolean {
        return false
    }
}