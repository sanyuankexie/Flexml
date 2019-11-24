package com.guet.flexbox.beans

import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal class ObjWrapper(private val o: Any) : AbstractMutableMap<String, Any?>() {

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
        o.javaClass.fields.filter {
            !Modifier.isStatic(it.modifiers)
        }.map {
            @Suppress("USELESS_CAST")
            Entry(it) as MutableMap.MutableEntry<String, Any?>
        }.toMutableSet()
    }

    override fun put(key: String, value: Any?): Any? {
        return entries.firstOrNull { it.key == key }?.setValue(value)
    }

    private inner class Entry(private val it: Field) : MutableMap.MutableEntry<String, Any?> {

        override val key: String
            get() = it.name
        override val value: Any?
            get() = it.get(o)

        override fun setValue(newValue: Any?): Any? {
            val old = it.get(o)
            it.set(o, newValue)
            return old
        }
    }
}