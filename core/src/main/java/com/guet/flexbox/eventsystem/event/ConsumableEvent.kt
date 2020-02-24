package com.guet.flexbox.eventsystem.event

import androidx.annotation.RestrictTo
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class ConsumableEvent<T, V>(
        source: T,
        private val context: JexlContext,
        private val script: JexlScript
) : TemplateEvent<T, V>(source) {
    private val lock = Any()

    protected open val args: Array<Any?> = emptyArray()

    private var trigger: Boolean = false

    @Suppress("UNCHECKED_CAST")
    override val value: V? by lazy(lock) {
        val result = script.execute(context, *args) as? V
        trigger = true
        return@lazy result
    }

    val isConsumed: Boolean
        get() = synchronized(lock) { trigger }

}