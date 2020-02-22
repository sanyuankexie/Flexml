package com.guet.flexbox.handshake

import org.springframework.context.ApplicationEvent

class NetworkChangedEvent(
        source: Any,
        val host: String
) : ApplicationEvent(source)