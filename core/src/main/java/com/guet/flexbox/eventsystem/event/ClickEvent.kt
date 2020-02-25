package com.guet.flexbox.eventsystem.event

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class ClickEvent(
        source: View,
        context: JexlContext,
        script: JexlScript
) : ExecutableEvent<View>(
        source,
        context,
        script
)