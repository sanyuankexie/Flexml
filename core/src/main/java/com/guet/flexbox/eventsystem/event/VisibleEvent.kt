package com.guet.flexbox.eventsystem.event

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class VisibleEvent(
        context: JexlContext,
        script: JexlScript
) : ConsumableEvent<Unit, Any?>(
        Unit, context, script
)