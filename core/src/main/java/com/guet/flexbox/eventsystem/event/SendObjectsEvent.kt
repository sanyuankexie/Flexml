package com.guet.flexbox.eventsystem.event

class SendObjectsEvent(
        override val value: Array<out Any?>
) : TemplateEvent<Unit, Array<out Any?>>(Unit)