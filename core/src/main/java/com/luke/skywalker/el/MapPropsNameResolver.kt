package com.luke.skywalker.el

internal class MapPropsNameResolver(
        private val map: MutableMap<String, Any?>,
        private val isReadOnly: Boolean = false
) : BeanNameResolver() {

    override fun setBeanValue(beanName: String, value: Any?) {
        map[beanName] = value
    }

    override fun getBean(beanName: String?): Any? {
        return map[beanName]
    }

    override fun isNameResolved(beanName: String?): Boolean {
        if (isReadOnly) {
            throw PropertyNotWritableException()
        }
        return map.containsKey(beanName)
    }

    override fun isReadOnly(beanName: String?): Boolean {
        return isReadOnly
    }
}