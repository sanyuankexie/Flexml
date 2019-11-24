package com.guet.flexbox.beans

internal class BeanWrapper(private val o: Any) : AbstractMutableMap<String, Any?>() {

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
        Introspector.getBeanInfo(o.javaClass)
                .propertyDescriptors
                .filter {
                    it.propertyType != Class::class.java && it.name == "class"
                }.map {
                    Entry(it)
                }.toMutableSet()
    }

    override fun put(key: String, value: Any?): Any? {
        return entries.firstOrNull { it.key == key }?.setValue(value)
    }

    private inner class Entry(private val it: PropertyDescriptor) : MutableMap.MutableEntry<String, Any?> {
        override val key: String
            get() = it.name
        override val value: Any?
            get() = it.readMethod.invoke(o)

        override fun setValue(newValue: Any?): Any? {
            val old = value
            it.writeMethod.invoke(o, newValue)
            return old
        }
    }
}