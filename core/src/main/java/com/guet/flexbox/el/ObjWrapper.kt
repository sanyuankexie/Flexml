package com.guet.flexbox.el

import com.guet.flexbox.beans.Introspector
import org.json.JSONObject
import java.lang.reflect.Modifier

private typealias Property = Pair<() -> Any?, (Any?) -> Unit>

private inline var Property.value: Any?
    get() = this.first()
    set(value) = this.second(value)

internal class ObjWrapper(private val o: Any) : BeanNameResolver() {

    private val props: HashMap<String, Property>

    init {
        val javaClass = o.javaClass
        when {
            o is JSONObject -> {
                props = HashMap(o.length())
                o.keys().forEach { key ->
                    props[key] = { o[key] } to { value -> o.put(key, value) }
                }
            }
            javaClass.methods.all { it.declaringClass == Any::class.java } -> {
                val fields = javaClass.fields
                props = HashMap(fields.size)
                fields.filter {
                    !Modifier.isStatic(it.modifiers)
                }.forEach { f ->
                    props[f.name] = { f.get(o) } to { value -> f.set(o, value) }
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
                    props[pd.name] = { read.invoke(o) } to { value -> write.invoke(o, value) }
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
        props[beanName]?.value = value
    }

    override fun isReadOnly(beanName: String?): Boolean {
        return false
    }
}