package com.guet.flexbox.handshake.gui

import com.guet.flexbox.handshake.NetworkHostAddress
import org.slf4j.LoggerFactory
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

@Component
class GuiComponent : ApplicationListener<WebServerInitializedEvent> {

    companion object {

        private const val WINDOW_SIZE: Int = 300
        private val logger = LoggerFactory.getLogger(GuiComponent::class.java)
    }

    private class Window(private val port: Int) : JFrame() {

        private var lastHostAddress: String? = null

        private lateinit var imageView: ImageView

        init {
            EventQueue.invokeLater {
                initUI()
                initWatcher()
            }
        }

        private fun initUI() {
            GuiComponent::class.java
                    .classLoader
                    .getResourceAsStream("static/icon.png")
                    ?.use {
                        iconImage = ImageIO.read(it)
                    }
            title = "Handshake"
            defaultCloseOperation = DISPOSE_ON_CLOSE
            setSize(WINDOW_SIZE, WINDOW_SIZE)
            isResizable = false
            val content = contentPane
            content.add(imageView)
            val kit = Toolkit.getDefaultToolkit()
            //获取屏幕的尺寸
            val screenSize = kit.screenSize
            //获取屏幕的宽
            val screenWidth = screenSize.width
            //获取屏幕的高
            val screenHeight = screenSize.height
            //设置窗口居中显示
            setLocation(
                    screenWidth / 2 - WINDOW_SIZE / 2,
                    screenHeight / 2 - WINDOW_SIZE / 2
            )
            addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    exitProcess(0)
                }
            })
            isVisible = true
            isAlwaysOnTop = true
            EventQueue.invokeLater {
                isAlwaysOnTop = false
            }
        }

        private fun initWatcher() {
            Timer().schedule(0, 2000) {
                val host = NetworkHostAddress.findHostAddress()
                if (host != null && host != lastHostAddress) {
                    lastHostAddress = host
                    val url = "http://$host:${port}"
                    logger.info("Network changed: $url")
                    val image = QrCodeImage.generate(url, WINDOW_SIZE)
                    EventQueue.invokeLater {
                        imageView.image = image
                    }
                }
            }
        }
    }

    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        System.clearProperty("java.awt.headless")
        if (GraphicsEnvironment.isHeadless()) {
            return
        } else {
            Window(event.webServer.port)
        }
    }
}