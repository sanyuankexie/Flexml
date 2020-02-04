package com.guet.flexbox.build.attrsinfo

import android.graphics.Color
import android.util.ArrayMap
import com.guet.flexbox.EventHandler
import com.guet.flexbox.EventContext
import com.guet.flexbox.build.AttributeInfoSet
import com.guet.flexbox.build.Converter
import com.guet.flexbox.el.ELContext

internal class AttrsInfoRegistry {
    private val _value = ArrayMap<String, AttributeInfo<*>>()

    fun text(
            name: String,
            scope: (Map<String, String>) = emptyMap(),
            fallback: String = ""
    ) {
        _value[name] = TextAttributeInfo(scope, fallback)
    }

    fun bool(
            name: String,
            scope: Map<String, Boolean> = emptyMap(),
            fallback: Boolean = false
    ) {
        _value[name] = BoolAttributeInfo(scope, fallback)
    }

    fun value(
            name: String,
            scope: Map<String, Double> = emptyMap(),
            fallback: Double = 0.0
    ) {
        _value[name] = ValueAttributeInfo(scope, fallback)
    }

    fun color(
            name: String,
            scope: Map<String, Int> = emptyMap(),
            fallback: Int = Color.TRANSPARENT
    ) {
        _value[name] = ColorAttributeInfo(scope, fallback)
    }

    inline fun <reified V : Enum<V>> enum(
            name: String,
            scope: Map<String, V>,
            fallback: V = enumValues<V>().first()
    ) {
        _value[name] = EnumAttributeInfo(scope, fallback)
    }

    inline fun event(
            name: String,
            crossinline action: Converter<EventHandler>
    ) {
        typed(name, action)
    }

    inline fun <T : Any> typed(
            name: String,
            crossinline action: Converter<T>
    ) {
        _value[name] = object : AttributeInfo<T>() {
            override fun cast(
                    eventContext: EventContext,
                    data: ELContext,
                    raw: String
            ): T? {
                return action(eventContext, data, raw)
            }
        }
    }

    val value: AttributeInfoSet
        get() = _value
}