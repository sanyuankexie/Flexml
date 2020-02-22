package com.guet.flexbox.handshake.ui

import com.guet.flexbox.handshake.NetworkChangedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.awt.EventQueue

@Component
class QrcodeFormManager {

    @Autowired
    private var qrcodeForm: QrcodeForm? = null

    @EventListener
    fun onInitialized(event: WebServerInitializedEvent) {
        qrcodeForm?.start()
    }

    @EventListener
    fun onNetworkChanged(event: NetworkChangedEvent) {
        EventQueue.invokeLater {
            qrcodeForm?.notifyChanged(event.host)
        }
    }
}