package com.guet.flexbox.eventsystem.event

import androidx.annotation.RestrictTo
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.concurrent.Callable

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
abstract class ExecutableEvent<T>(
        source: T,
        private val context: JexlContext,
        private val script: JexlScript
) : TemplateEvent<T>(source), Callable<Any> {

    protected open val args: Array<Any?>
        get() = emptyArray()

    override fun call(): Any? {
        return script.execute(context, *args)
    }
}