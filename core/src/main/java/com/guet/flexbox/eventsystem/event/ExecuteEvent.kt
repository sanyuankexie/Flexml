package com.guet.flexbox.eventsystem.event

import androidx.annotation.RestrictTo
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ExecuteEvent(
        context: JexlContext,
        script: JexlScript
) : ConsumableEvent<Unit, Unit>(
        Unit,
        context,
        script
)