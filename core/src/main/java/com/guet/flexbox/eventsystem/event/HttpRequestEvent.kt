package com.guet.flexbox.eventsystem.event

import com.guet.flexbox.http.HttpRequest

class HttpRequestEvent(
        val httpRequest: HttpRequest
) : TemplateEvent<Unit>(Unit)