package com.guet.flexbox.eventsystem.event

import com.guet.flexbox.HttpRequest

class HttpRequestEvent(
        override val value: HttpRequest
) : TemplateEvent<Unit, HttpRequest>(Unit)