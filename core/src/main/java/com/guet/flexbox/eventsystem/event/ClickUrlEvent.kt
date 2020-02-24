package com.guet.flexbox.eventsystem.event

import android.view.View

class ClickUrlEvent(
        source: View,
        override val value: String?
) : TemplateEvent<View, String>(source)