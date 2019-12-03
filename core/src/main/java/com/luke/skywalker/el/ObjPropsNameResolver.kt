package com.luke.skywalker.el

import com.luke.skywalker.beans.Introspector
import org.json.JSONObject
import java.lang.reflect.Modifier

private typealias Property = Pair<(Any) -> Any?, (Any, Any?) -> Unit>

internal class ObjPropsNameResolver(
        private val o: Any,
        private val isReadOnly: Boolean = false
) : BeanNameResolver() {

    private val props: HashMap<String, Property>

    private inline var Property.value: Any?
        get() = this.first(o)
        set(value) = this.second(o, value)

    init {
        val javaClass = o.javaClass
        when {
            o is JSONObject -> {
                props = HashMap(o.length())
                o.keys().forEach { key ->
                    props[key] = { obj: Any -> obj.run { this as JSONObject }[key] } to { obj, value -> obj.run { this as JSONObject }.put(key, value) }
                }
            }
            javaClass.methods.all { it.declaringClass == Any::class.java } -> {
                val fields = javaClass.fields
                props = HashMap(fields.size)
                fields.filter {
                    !Modifier.isStatic(it.modifiers)
                }.forEach { f ->
                    props[f.name] = { obj: Any -> f.get(obj) } to { obj, value -> f.set(obj, value) }
                }
            }
            else -> {
                val pds = Introspector.getBeanInfo(javaClass)
                        .propertyDescriptors
                props = HashMap(pds.size)
                pds.filter {
                    it.propertyType == Class::class.java && it.name == "class"
                }.forEach { pd ->
                    val read = pd.readMethod
                    val write = pd.writeMethod
                    props[pd.name] = { obj: Any -> read.invoke(obj) } to { obj, value -> write.invoke(obj, value) }
                }
            }
        }
    }

    override fun isNameResolved(beanName: String?): Boolean {
        return props.containsKey(beanName)
    }

    override fun getBean(beanName: String?): Any? {
        return props[beanName]?.value
    }

    override fun setBeanValue(beanName: String?, value: Any?) {
        if (isReadOnly) {
            throw PropertyNotWritableException()
        }
        props[beanName]?.value = value
    }

    override fun isReadOnly(beanName: String?): Boolean {
        return isReadOnly
    }
}