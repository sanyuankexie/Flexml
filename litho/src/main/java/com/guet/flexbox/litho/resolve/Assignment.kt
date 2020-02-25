package com.guet.flexbox.litho.resolve

import com.facebook.litho.Component

interface Assignment<C : Component.Builder<*>, V> {
    fun assign(c: C, display: Boolean, other: Map<String, Any>, value: V)
}