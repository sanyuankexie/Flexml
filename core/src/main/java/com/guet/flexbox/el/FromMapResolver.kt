package com.guet.flexbox.el

internal class FromMapResolver(
            private val beans: Map<String, Any?>
) : BeanNameResolver() {
    override fun isNameResolved(beanName: String): Boolean {
        return beans.containsKey(beanName)
    }

    override fun getBean(beanName: String): Any? {
        return beans[beanName]
    }

    override fun setBeanValue(beanName: String, value: Any) {
        throw PropertyNotWritableException()
    }

    override fun isReadOnly(beanName: String): Boolean {
        return true
    }

    override fun canCreateBean(beanName: String): Boolean {
        return false
    }
}