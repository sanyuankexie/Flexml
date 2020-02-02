package com.guet.flexbox.handshake.gui

import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.net.URI

@Component
class GuiQrCodeApplication : ApplicationListener<WebServerInitializedEvent> {

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        System.clearProperty("java.awt.headless")
        if (GraphicsEnvironment.isHeadless()) {
            return
        } else {
            val port = event.webServer.port
            EventQueue.invokeLater {
                val window = QrCodeWindow()
                window.start(port)
                if (Desktop.isDesktopSupported()) {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI("http://localhost:$port"))
                    }
                }
            }
        }
    }
}