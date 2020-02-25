package com.guet.flexbox.eventsystem.event

class SendObjectsEvent(
        val values: Array<out Any?>
) : TemplateEvent<Unit>(Unit)