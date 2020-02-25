package com.guet.flexbox.eventsystem.event

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.concurrent.Callable

class ClickExprEvent(
        source: View,
        context: JexlContext,
        script: JexlScript
) : ClickEvent(source), HasExprEvent {
    override val expr: Callable<Any> = script.callable(context)
}