package com.guet.flexbox.eventsystem.event

import android.view.View
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlScript

class ClickUrlEvent(
        source: View,
        val url: String
) : ClickEvent(source) {

}