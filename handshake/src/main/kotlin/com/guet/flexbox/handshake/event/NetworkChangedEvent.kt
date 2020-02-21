package com.guet.flexbox.handshake.event

import org.springframework.context.ApplicationEvent

class NetworkChangedEvent(
        source: Any,
        val host: String
) : ApplicationEvent(source)