package com.guet.flexbox.litho.resolve

import android.util.ArrayMap
import com.facebook.litho.Component

abstract class Matcher<C : Component.Builder<*>>(
        private val component: C,
        private val display: Boolean,
        private val attrs: Map<String, Any>
) {

    private val missMatch = ArrayMap<String, Any>()

    init {
        missMatch.putAll(attrs)
    }

    fun match(): Map<String, Any> {
        onMatch()
        return missMatch
    }

    protected abstract fun onMatch()

    internal inline fun <reified T> register(
            name: String,
            assignment: Assignment<C, T>
    ) {
        val obj = attrs[name]
        if (obj is T) {
            component.assignment(display, attrs, obj)
            missMatch.remove(name)
        }
    }
}



