package com.guet.flexbox.el

import org.json.JSONArray
import java.lang.reflect.Method
import java.util.*

class PropsELContext(
        val data: Any?
) : ELContext() {

    private val variableMapper = StandardVariableMapper()
    private val functionMapper = StandardFunctionMapper()
    private val standardResolver = CompositeELResolver()

    init {
        val props = createPropELResolver(data)
        if (props != null) {
            standardResolver.add(props)
        }
        standardResolver.add(expressionFactory.streamELResolver)
        standardResolver.add(staticField)
        standardResolver.add(map)
        standardResolver.add(resources)
        standardResolver.add(list)
        standardResolver.add(array)
        standardResolver.add(bean)
        standardResolver.add(jsonObject)
        standardResolver.add(jsonArray)
    }

    @Throws(ELException::class)
    internal fun ELContext.getValue(expr: String, type: Class<*>): Any {
        return synchronized(expressionFactory) {
            expressionFactory.createValueExpression(
                    this,
                    expr,
                    type
            ).getValue(this) ?: throw ELException("$expr out null")
        }
    }

    override fun getELResolver(): ELResolver = standardResolver

    override fun getFunctionMapper(): FunctionMapper = functionMapper

    override fun getVariableMapper(): VariableMapper = variableMapper

    private class StandardVariableMapper : VariableMapper() {

        private lateinit var vars: MutableMap<String, ValueExpression>

        override fun resolveVariable(variable: String): ValueExpression? {
            return if (!this::vars.isInitialized) {
                null
            } else vars[variable]
        }

        override fun setVariable(variable: String,
                                 expression: ValueExpression?
        ): ValueExpression? {
            if (!this::vars.isInitialized)
                vars = HashMap()
            return if (expression == null) {
                vars.remove(variable)
            } else {
                vars.put(variable, expression)
            }
        }
    }

    private class StandardFunctionMapper : FunctionMapper() {

        private val methods = HashMap<String, Method>(ELFunctions.functions)

        override fun resolveFunction(
                prefix: String,
                localName: String
        ): Method? {
            val key = "$prefix:$localName"
            return methods[key]
        }

        override fun mapFunction(
                prefix: String,
                localName: String,
                method: Method?
        ) {
            val key = "$prefix:$localName"
            if (method == null) {
                methods.remove(key)
            } else {
                methods[key] = method
            }
        }
    }

    private companion object {

        private val array = ArrayELResolver(false)
        private val bean = BeanELResolver(false)
        private val map = MapELResolver(false)
        private val list = ListELResolver(false)
        private val jsonObject = JSONObjectELResolver(false)
        private val jsonArray = JSONArrayELResolver(false)
        private val staticField = StaticFieldELResolver()
        private val resources = ResourceBundleELResolver()

        private fun createPropELResolver(data: Any?): ELResolver? {
            return when {
                data is Map<*, *> && data.keys.all { it is String } -> {
                    PropsELResolver(data, map)
                }
                data is JSONArray -> {
                    PropsELResolver(data, jsonObject)
                }
                data != null -> {
                    PropsELResolver(data, bean)
                }
                else -> {
                    return null
                }
            }
        }

    }

}
