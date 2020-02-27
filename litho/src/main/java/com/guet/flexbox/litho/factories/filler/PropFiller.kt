package com.guet.flexbox.litho.factories.filler

import com.facebook.litho.Component

internal interface PropFiller<C : Component.Builder<*>, V> {
    fun fill(c: C, display: Boolean, other: Map<String, Any>, value: V)
}