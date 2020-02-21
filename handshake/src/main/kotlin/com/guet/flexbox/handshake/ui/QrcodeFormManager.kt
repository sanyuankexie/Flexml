package com.guet.flexbox.handshake.ui

import com.guet.flexbox.handshake.event.NetworkChangedEvent
import com.guet.flexbox.handshake.event.PortHasSetEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.system.exitProcess

@Component
class QrcodeFormManager {

    private companion object {
        private const val WINDOW_SIZE: Int = 300
    }

    private class QrcodeForm(
            private val attributes: ConcurrentHashMap<String, Any>
    ) : JFrame() {

        val imageView: ImageView

        init {
            javaClass.classLoader
                    .getResourceAsStream("static/icon.png")
                    ?.use {
                        iconImage = ImageIO.read(it)
                    }
            title = "Handshake"
            defaultCloseOperation = DISPOSE_ON_CLOSE
            setSize(WINDOW_SIZE, WINDOW_SIZE)
            isResizable = false
            val content = contentPane
            imageView = ImageView()
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
                val port = attributes["port"] as? Int ?: 8080
                if (Desktop.isDesktopSupported()) {
                    val desktop = Desktop.getDesktop()
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(URI("http://localhost:$port"))
                    }
                }
                isAlwaysOnTop = false
            }
        }

        fun notifyChanged(host: String) {
            val port = attributes["port"] as? Int ?: 8080
            val url = "http://$host:${port}"
            val image = QrcodeImageUtils
                    .buildImage(url, WINDOW_SIZE)
            EventQueue.invokeLater {
                imageView.image = image
            }
        }
    }

    @Volatile
    private var qrcodeForm: QrcodeForm? = null

    @Autowired
    private lateinit var attributes: ConcurrentHashMap<String, Any>

    @EventListener
    fun onInitialized(event: PortHasSetEvent) {
        System.clearProperty("java.awt.headless")
        if (!GraphicsEnvironment.isHeadless()) {
            EventQueue.invokeLater {
                val form = QrcodeForm(attributes)
                form.isVisible = true
                qrcodeForm = form
            }
        }
    }

    @EventListener
    fun onNetworkChanged(event: NetworkChangedEvent) {
        EventQueue.invokeLater {
            qrcodeForm?.notifyChanged(event.host)
        }
    }
}