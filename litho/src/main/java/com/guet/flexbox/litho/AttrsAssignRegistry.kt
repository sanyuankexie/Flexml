package com.guet.flexbox.litho

import android.util.ArrayMap
import com.facebook.litho.Component

internal class AttrsAssignRegistry<C : Component.Builder<*>> {

    private val _value = ArrayMap<String, Assignment<C, *>>()

    fun <T> register(
            name: String,
            assignment: Assignment<C, T>
    ) {
        _value[name] = assignment
    }

    val value: AttributeAssignSet<C>
        get() = _value
}