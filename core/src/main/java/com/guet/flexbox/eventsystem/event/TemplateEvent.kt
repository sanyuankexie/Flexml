package com.guet.flexbox.eventsystem.event

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript
import java.util.*

abstract class TemplateEvent<T>(
        source: T
) : EventObject(source) {
    override fun getSource(): T {
        @Suppress("UNCHECKED_CAST")
        return super.getSource() as T
    }

    interface Factory {
        fun create(
                source: View?,
                args: Array<out Any?>?,
                dataContext: JexlContext,
                script: JexlScript
        ): TemplateEvent<*>
    }
}