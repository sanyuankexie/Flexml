package com.guet.flexbox.litho.factories

import com.facebook.litho.Component
import com.guet.flexbox.build.AttributeSet

internal typealias Assignment<C, V> = C.(display: Boolean, other: Map<String, Any>, value: V) -> Unit

internal typealias AttributeAssignSet<C> = Map<String, Assignment<C, *>>

internal inline fun <T : Component.Builder<*>> create(
        crossinline action: AttrsAssignRegistry<T>.() -> Unit
): Lazy<AttributeAssignSet<T>> {
    return lazy {
        AttrsAssignRegistry<T>().apply(action).value
    }
}

internal fun AttributeSet.getFloatValue(name: String): Float {
    return (this[name] as? Double)?.toFloat() ?: 0f
}