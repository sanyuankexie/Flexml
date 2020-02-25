package com.guet.flexbox.eventsystem.event

import com.guet.flexbox.HttpRequest

class HttpRequestEvent(
        val httpRequest: HttpRequest
) : TemplateEvent<Unit>(Unit)