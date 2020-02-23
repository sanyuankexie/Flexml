package com.guet.flexbox.litho.resolve

import com.facebook.litho.Component
import com.guet.flexbox.build.AttributeSet

internal typealias Assignment<C, V> = C.(display: Boolean, other: Map<String, Any>, value: V) -> Unit

internal fun AttributeSet.getFloatValue(name: String): Float {
    return (this[name] as? Float) ?: 0f
}

internal inline fun <C : Component.Builder<*>> createProvider(
        crossinline action: Matcher<C>.() -> Unit
): Matcher.Provider<C> {
    return object : Matcher.Provider<C> {
        override fun get(
                component: C,
                display: Boolean,
                attrs: Map<String, Any>
        ): Matcher<C> {
            return object : Matcher<C>(component, display, attrs) {
                override fun onMatch() {
                    action()
                }
            }
        }
    }
}

fun <T> Enum<*>.mapping(): T {
    return EnumMappings.get(this)
}
