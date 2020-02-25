package com.guet.flexbox.eventsystem.event

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.concurrent.Callable

class VisibleEvent(
        context: JexlContext,
        script: JexlScript
) : HasExprEvent {
    override val expr: Callable<Any> = script.callable(context)
}